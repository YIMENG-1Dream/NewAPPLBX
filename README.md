# WebLXBapp - 六边形计算器

![应用图标](app/src/main/res/mipmap-xxhdpi/ic_launcher.png)

一个基于WebView的Android应用，用于显示六边形计算器（多边形计算机）成绩分析工具。

## 功能特性

- 📊 显示完整的HTML网页应用
- 🔄 支持下拉刷新
- 📱 移动端优化界面
- 💾 本地数据存储
- 📤 数据导出功能
- 🎨 多主题切换
- 🌐 内置Chart.js图表库
- 🔧 Android原生功能集成

## 软件信息

- **软件名称**: 六边形计算器
- **软件编号**: LXB2025001
- **版本**: v1.0.0
- **开发者**: Destination_ovo
- **邮箱**: 2211717081@qq.com

## 技术栈

- Android Studio
- Java/Kotlin
- WebView
- Chart.js
- HTML5/CSS3/JavaScript

## 构建说明

### 环境要求

- Android Studio 2022+
- JDK 11+
- Gradle 8.0+

### 构建步骤

1. 克隆项目
2. 使用Android Studio打开项目
3. 生成签名密钥：
   ```bash
   keytool -genkey -v -keystore app-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias weblxbapp