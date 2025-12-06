package com.destinationovo.weblxbapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.JavascriptInterface;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

public class WebAppInterface {
    private Context context;
    private MainActivity mainActivity;
    
    public WebAppInterface(Context context) {
        this.context = context;
        if (context instanceof MainActivity) {
            this.mainActivity = (MainActivity) context;
        }
    }
    
    @JavascriptInterface
    public void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    
    @JavascriptInterface
    public void showAndroidDialog(String title, String message) {
        new AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("确定", null)
            .show();
    }
    
    @JavascriptInterface
    public void exportDataToAndroid(String jsonData) {
        if (mainActivity != null) {
            mainActivity.exportData(jsonData);
        }
    }
    
    @JavascriptInterface
    public String getAppInfo() {
        return "WebLXBapp v" + BuildConfig.VERSION_NAME + 
               " | 编号: " + BuildConfig.APP_SERIAL +
               " | 开发者: " + BuildConfig.DEVELOPER_NAME;
    }
    
    @JavascriptInterface
    public void openExternalBrowser(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "无法打开链接", Toast.LENGTH_SHORT).show();
        }
    }
    
    @JavascriptInterface
    public void shareData(String title, String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        
        Intent chooser = Intent.createChooser(shareIntent, "分享数据");
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(chooser);
    }
    
    @JavascriptInterface
    public void setClipboard(String text) {
        android.content.ClipboardManager clipboard = 
            (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("六边形计算器数据", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "已复制到剪贴板", Toast.LENGTH_SHORT).show();
    }
}