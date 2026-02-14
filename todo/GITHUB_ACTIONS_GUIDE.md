# 🚀 使用 GitHub Actions 构建 APK

## 📱 快速开始

### 方法一：使用 GitHub Actions（推荐）

这是最简单的方法，无需本地配置！

#### 步骤 1：上传到 GitHub

1. 创建 GitHub 账号（如果没有）：https://github.com/signup
2. 创建新仓库：https://github.com/new
   - 仓库名：`todo-app`
   - 设为 Public 或 Private 都可以
3. 在本地初始化 Git：
   ```bash
   cd d:\03-Work\todo
   git init
   git add .
   git commit -m "Initial commit"
   ```
4. 关联远程仓库并推送：
   ```bash
   git remote add origin https://github.com/你的用户名/todo-app.git
   git branch -M main
   git push -u origin main
   ```

#### 步骤 2：触发构建

推送到 GitHub 后，会自动触发构建，或者手动触发：

**自动触发**：
- 推送代码到 `main` 或 `master` 分支
- 创建 Pull Request

**手动触发**：
1. 访问你的 GitHub 仓库
2. 点击 "Actions" 标签
3. 选择 "Build Android APK" 工作流
4. 点击 "Run workflow" 按钮
5. 选择 "Run workflow" 确认

#### 步骤 3：下载 APK

1. 等待构建完成（约 5-10 分钟）
2. 在 Actions 页面找到成功的构建（绿色勾）
3. 点击进入构建详情
4. 滚动到底部，找到 "Artifacts" 部分
5. 点击 `app-debug` 下载 ZIP 文件
6. 解压 ZIP，得到 `app-debug.apk`

### 方法二：使用其他电脑的 Android Studio

如果您有其他电脑安装了 Android Studio：

1. 将整个 `d:\03-Work\todo` 文件夹复制到 U 盘
2. 在有 Android Studio 的电脑上打开项目
3. 点击 Build → Build Bundle(s) / APK(s) → Build APK(s)
4. 复制生成的 APK 到手机

### 方法三：使用在线 IDE

#### Replit
1. 访问 https://replit.com/
2. 创建新的 Android 项目
3. 上传所有项目文件
4. 在终端运行：`./gradlew assembleDebug`
5. 下载生成的 APK

#### Gitpod
1. 访问 https://gitpod.io/
2. 导入 GitHub 仓库
3. 在终端运行：`./gradlew assembleDebug`
4. 下载生成的 APK

## 📱 安装到手机

### 方法一：直接安装

1. 将 `app-debug.apk` 传输到手机
2. 在手机上点击 APK 文件
3. 允许安装未知来源（如果需要）
4. 完成安装

### 方法二：使用 ADB

```bash
# 连接手机（USB 调试已开启）
adb devices

# 安装
adb install app-debug.apk

# 启动应用
adb shell am start -n com.example.todo/.MainActivity
```

### 方法三：使用模拟器

```bash
# 创建模拟器
avdmanager create avd -n test_device -k "system-images;android-34;google_apis;x86_64"

# 启动模拟器
emulator -avd test_device

# 安装到模拟器
adb install app-debug.apk
```

## 🔧 构建配置

### GitHub Actions 工作流

项目已配置好 `.github/workflows/build.yml`，会自动：
- ✅ 检出代码
- ✅ 设置 JDK 17
- ✅ 构建 Debug APK
- ✅ 上传 APK 作为构建产物

### 构建产物

- **文件名**：`app-debug.apk`
- **位置**：`app/build/outputs/apk/debug/`
- **保留时间**：30 天
- **下载链接**：在 GitHub Actions 页面

## 📋 检查清单

在推送前，确保：

- [ ] 所有代码文件完整
- [ ] `.github/workflows/build.yml` 存在
- [ ] `.gitignore` 文件存在
- [ ] `build.gradle.kts` 配置正确
- [ ] `AndroidManifest.xml` 权限完整

## 🎯 推荐流程

### 最简单（推荐）
```
1. 上传到 GitHub
2. 等待自动构建
3. 下载 APK
4. 安装到手机
```

### 最快速
```
1. 使用其他电脑的 Android Studio
2. 构建 APK
3. 传输到手机
```

### 最专业
```
1. 配置本地 Android SDK
2. 本地构建
3. 调试和测试
```

## 📊 构建时间

| 方法 | 时间 | 说明 |
|------|------|------|
| GitHub Actions | 5-10 分钟 | 首次构建较慢 |
| Android Studio | 3-5 分钟 | 最快 |
| 在线 IDE | 10-15 分钟 | 需要上传文件 |
| 命令行工具 | 10-20 分钟 | 需要配置环境 |

## 🔍 故障排除

### 构建失败

**错误：Plugin not found**
- 解决：检查 `build.gradle.kts` 中的插件版本
- 解决：等待 GitHub Actions 使用最新环境

**错误：Compilation failed**
- 解决：检查代码语法错误
- 解决：查看构建日志了解详情

**错误：Permission denied**
- 解决：检查 `gradlew` 文件权限
- 解决：GitHub Actions 会自动处理

### 下载失败

**错误：Artifact not found**
- 解决：等待构建完全完成
- 解决：刷新 Actions 页面

**错误：Download failed**
- 解决：检查网络连接
- 解决：尝试使用不同的浏览器

## 📚 相关文档

- [BUILD_APK_GUIDE.md](BUILD_APK_GUIDE.md) - 详细的构建指南
- [README.md](README.md) - 项目概述
- [FEATURES.md](FEATURES.md) - 功能说明

## 🎉 开始使用

选择最适合您的方法，开始构建 APK 并安装到手机！

**推荐使用 GitHub Actions**，因为：
- ✅ 无需本地环境
- ✅ 自动化构建
- ✅ 可重复使用
- ✅ 完全免费

祝您使用愉快！🚀
