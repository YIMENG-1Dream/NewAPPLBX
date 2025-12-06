package com.destinationovo.weblxbapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.destinationovo.weblxbapp.databinding.ActivityMainBinding;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {
    
    private ActivityMainBinding binding;
    private WebAppInterface webAppInterface;
    private Handler handler = new Handler(Looper.getMainLooper());
    
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // 设置标题
        setTitle(getString(R.string.app_name_full));
        
        // 初始化WebView
        setupWebView();
        
        // 设置下拉刷新
        setupSwipeRefresh();
        
        // 显示关于信息
        showAboutInfo();
    }
    
    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings webSettings = binding.webView.getSettings();
        
        // 启用JavaScript
        webSettings.setJavaScriptEnabled(true);
        
        // 启用DOM存储
        webSettings.setDomStorageEnabled(true);
        
        // 启用数据库
        webSettings.setDatabaseEnabled(true);
        
        // 设置缓存
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setAppCacheEnabled(true);
        
        // 允许文件访问
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        
        // 设置用户代理
        webSettings.setUserAgentString("WebLXBapp/" + BuildConfig.VERSION_NAME + " Android");
        
        // 设置视口
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        
        // 创建WebApp接口
        webAppInterface = new WebAppInterface(this);
        binding.webView.addJavascriptInterface(webAppInterface, "Android");
        
        // 设置WebView客户端
        binding.webView.setWebViewClient(new MyWebViewClient());
        
        // 设置Chrome客户端
        binding.webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                binding.progressBar.setProgress(newProgress);
                binding.progressBar.setVisibility(newProgress == 100 ? android.view.View.GONE : android.view.View.VISIBLE);
            }
            
            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (title != null && !title.startsWith("file://")) {
                    setTitle(title + " - " + getString(R.string.app_name_full));
                }
            }
        });
        
        // 加载本地HTML文件
        binding.webView.loadUrl("file:///android_asset/web/index.html");
    }
    
    private void setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            binding.webView.reload();
            binding.swipeRefreshLayout.setRefreshing(false);
        });
        
        // 设置刷新颜色
        binding.swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        );
    }
    
    private void showAboutInfo() {
        handler.postDelayed(() -> {
            String aboutInfo = "软件名称: " + BuildConfig.APP_NAME + "\n" +
                             "版本: v" + BuildConfig.VERSION_NAME + "\n" +
                             "编号: " + BuildConfig.APP_SERIAL + "\n" +
                             "开发者: " + BuildConfig.DEVELOPER_NAME + "\n\n" +
                             "功能: 成绩分析与可视化工具";
            
            Toast.makeText(this, aboutInfo, Toast.LENGTH_LONG).show();
        }, 1000);
    }
    
    // 自定义WebViewClient
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            
            // 拦截外部链接，在WebView中打开
            if (url.startsWith("http://") || url.startsWith("https://")) {
                view.loadUrl(url);
                return true;
            }
            
            // 处理mailto链接
            if (url.startsWith("mailto:")) {
                try {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "无法打开邮件客户端", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            
            return false;
        }
        
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            binding.progressBar.setVisibility(android.view.View.VISIBLE);
            binding.progressBar.setProgress(0);
        }
        
        @Override
        public void onPageFinished(WebView view, String url) {
            binding.progressBar.setVisibility(android.view.View.GONE);
            
            // 注入Android设备信息到JavaScript
            injectDeviceInfo();
        }
        
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            
            // 拦截Chart.js请求，使用本地版本
            if (url.contains("cdn.jsdelivr.net/npm/chart.js")) {
                try {
                    InputStream is = getAssets().open("web/chart.js");
                    return new WebResourceResponse("application/javascript", "utf-8", is);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            return super.shouldInterceptRequest(view, request);
        }
    }
    
    private void injectDeviceInfo() {
        String js = String.format(
            "if (typeof window.polygonCalculator !== 'undefined') {" +
            "  window.polygonCalculator.deviceType = 'mobile';" +
            "  window.polygonCalculator.isAndroidApp = true;" +
            "  window.polygonCalculator.appVersion = '%s';" +
            "  window.polygonCalculator.appName = '%s';" +
            "  console.log('Android App Info injected');" +
            "}",
            BuildConfig.VERSION_NAME,
            BuildConfig.APP_NAME
        );
        
        binding.webView.evaluateJavascript(js, null);
    }
    
    // 处理返回键
    @Override
    public void onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack();
        } else {
            showExitDialog();
        }
    }
    
    private void showExitDialog() {
        new AlertDialog.Builder(this)
            .setTitle("退出应用")
            .setMessage("确定要退出六边形计算器吗？")
            .setPositiveButton("确定", (dialog, which) -> finish())
            .setNegativeButton("取消", null)
            .show();
    }
    
    // 导出数据功能
    public void exportData(String jsonData) {
        // 这里可以实现数据导出功能
        // 例如：保存到文件、分享等
        Toast.makeText(this, "数据已准备导出", Toast.LENGTH_SHORT).show();
        
        // 创建分享Intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/json");
        shareIntent.putExtra(Intent.EXTRA_TEXT, jsonData);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "六边形计算器数据导出");
        
        startActivity(Intent.createChooser(shareIntent, "导出数据"));
    }
    
    // 打印功能
    private void printWebView() {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter printAdapter = binding.webView.createPrintDocumentAdapter("六边形计算器报告");
        
        String jobName = "六边形计算器_" + System.currentTimeMillis();
        printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
    }
    
    @Override
    protected void onDestroy() {
        if (binding.webView != null) {
            binding.webView.destroy();
        }
        super.onDestroy();
    }
}