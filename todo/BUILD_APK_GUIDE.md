# 📱 获取 Android 安装包 (APK) 指南

由于当前开发环境缺少 Android SDK 和 Gradle 插件，无法直接构建 APK。以下是几种获取安装包的方法：

## 🚀 方法一：使用 GitHub Actions 自动构建（推荐）

### 1. 创建 GitHub 仓库

将项目上传到 GitHub：
```bash
# 初始化 Git 仓库
git init
git add .
git commit -m "Initial commit"

# 创建 GitHub 仓库后，关联远程仓库
git remote add origin https://github.com/你的用户名/todo-app.git
git push -u origin main
```

### 2. 配置 GitHub Actions

在项目根目录创建 `.github/workflows/build.yml`：

```yaml
name: Build Android APK

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - name: Checkout code
      uses: actions/checkout@v3

    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'

    - name: Grant execute permission for gradlew
      run: chmod +x gradlew

    - name: Build Debug APK
      run: ./gradlew assembleDebug

    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: app-debug
        path: app/build/outputs/apk/debug/app-debug.apk
```

### 3. 触发构建

推送到 GitHub 后，GitHub Actions 会自动构建：
1. 访问你的 GitHub 仓库
2. 点击 "Actions" 标签
3. 选择 "Build Android APK" 工作流
4. 点击 "Run workflow" 手动触发
5. 等待构建完成（约 5-10 分钟）

### 4. 下载 APK

构建完成后：
1. 在 Actions 页面找到成功的构建
2. 点击进入构建详情
3. 在 "Artifacts" 部分下载 `app-debug.apk`

## 🌐 方法二：使用在线构建服务

### 1. Replit

1. 访问 https://replit.com/
2. 创建新的 Android 项目
3. 上传项目文件
4. 在终端运行：
   ```bash
   ./gradlew assembleDebug
   ```
5. 下载生成的 APK

### 2. Gitpod

1. 访问 https://gitpod.io/
2. 导入 GitHub 仓库
3. 在终端运行：
   ```bash
   ./gradlew assembleDebug
   ```
4. 下载生成的 APK

### 3. CodeSandbox

1. 访问 https://codesandbox.io/
2. 创建新的 Android 项目
3. 上传项目文件
4. 构建并下载 APK

## 💻 方法三：在有 Android Studio 的电脑上构建

### 1. 安装 Android Studio

下载并安装：https://developer.android.com/studio

### 2. 打开项目

1. 启动 Android Studio
2. 选择 "Open an Existing Project"
3. 选择项目目录：`d:\03-Work\todo`
4. 等待 Gradle 同步完成

### 3. 构建 APK

在 Android Studio 中：
1. 点击菜单：Build → Build Bundle(s) / APK(s) → Build APK(s)
2. 等待构建完成
3. APK 位置：`app/build/outputs/apk/debug/app-debug.apk`

### 4. 传输到手机

方法 A：USB 传输
```bash
# 连接手机到电脑
# 复制 APK 到手机
adb push app-debug.apk /sdcard/Download/

# 安装
adb install app-debug.apk
```

方法 B：云盘传输
1. 上传 APK 到云盘（百度网盘、Google Drive 等）
2. 在手机上下载并安装

## 📦 方法四：使用预构建的 APK（如果可用）

如果之前有构建过，可以：
1. 检查 `app\build\outputs\apk\debug\` 目录
2. 查找 `app-debug.apk` 文件
3. 直接传输到手机安装

## 🔧 方法五：完整配置本地环境

### 1. 安装 Android SDK 命令行工具

下载：https://developer.android.com/studio#command-tools

解压到：`C:\Android\sdk\cmdline-tools\latest`

### 2. 配置环境变量

在系统环境变量中添加：
```
ANDROID_HOME = C:\Android\sdk
ANDROID_SDK_ROOT = C:\Android\sdk
```

在 PATH 中添加：
```
%ANDROID_HOME%\cmdline-tools\latest\bin
%ANDROID_HOME%\platform-tools
%ANDROID_HOME%\emulator
```

### 3. 安装必要的 SDK 组件

打开命令提示符（管理员权限）：
```powershell
# 接受许可
sdkmanager --licenses

# 安装平台
sdkmanager "platform-tools"
sdkmanager "platforms;android-34"
sdkmanager "build-tools;34.0.0"

# 安装系统镜像（用于模拟器）
sdkmanager "system-images;android-34;google_apis;x86_64"
```

### 4. 构建 APK

```powershell
cd d:\03-Work\todo

# 清理
.\gradlew.bat clean

# 构建
.\gradlew.bat assembleDebug

# APK 位置
# app\build\outputs\apk\debug\app-debug.apk
```

## 📱 安装到手机

### 方法一：直接安装

1. 将 APK 文件传输到手机
2. 在手机上点击 APK 文件
3. 允许安装未知来源（如果需要）
4. 完成安装

### 方法二：使用 ADB

```powershell
# 连接手机（USB 调试已开启）
adb devices

# 安装
adb install app-debug.apk

# 启动应用
adb shell am start -n com.example.todo/.MainActivity
```

### 方法三：使用模拟器

```powershell
# 创建模拟器
avdmanager create avd -n test_device -k "system-images;android-34;google_apis;x86_64"

# 启动模拟器
emulator -avd test_device

# 安装到模拟器
adb install app-debug.apk
```

## 🎯 推荐方案

### 最简单：GitHub Actions
- ✅ 无需本地环境
- ✅ 自动构建
- ✅ 可重复使用
- ✅ 免费使用

### 最快速：使用其他电脑
- ✅ 如果有其他电脑有 Android Studio
- ✅ 直接构建即可
- ✅ 无需配置环境

### 最专业：配置本地环境
- ✅ 完整的开发能力
- ✅ 可以修改和调试
- ✅ 一次配置，永久使用

## 📋 构建检查清单

在构建前，确保：

- [ ] 所有代码文件完整
- [ ] `build.gradle.kts` 配置正确
- [ ] `AndroidManifest.xml` 权限完整
- [ ] 所有依赖项正确
- [ ] 没有编译错误

## 🔍 验证 APK

构建完成后，验证：

1. **文件大小**：通常 5-15 MB
2. **文件名**：`app-debug.apk`
3. **签名**：Debug 版本使用 debug 签名
4. **权限**：检查 AndroidManifest.xml 中的权限

## 📊 构建时间参考

| 方法 | 时间 | 难度 |
|------|------|--------|
| GitHub Actions | 5-10 分钟 | ⭐ 简单 |
| 在线构建服务 | 10-15 分钟 | ⭐⭐ 中等 |
| Android Studio | 3-5 分钟 | ⭐⭐⭐ 简单 |
| 命令行工具 | 10-20 分钟 | ⭐⭐⭐⭐ 困难 |

## 🎉 总结

推荐使用 **GitHub Actions** 方法，因为：
- 无需配置本地环境
- 自动化构建流程
- 可重复使用
- 完全免费

选择最适合您的方法，获取 APK 并安装到手机上！📱✨
