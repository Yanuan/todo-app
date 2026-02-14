# 无 Android Studio 运行调试指南

## 方案一：使用命令行工具（推荐）

### 1. 安装 Android SDK 命令行工具

#### Windows 系统

1. **下载 Android SDK 命令行工具**
   - 访问：https://developer.android.com/studio#command-tools
   - 下载 "Command line tools only" for Windows

2. **解压并配置环境变量**
   ```powershell
   # 创建 SDK 目录
   mkdir C:\Android\sdk
   mkdir C:\Android\sdk\cmdline-tools
   mkdir C:\Android\sdk\cmdline-tools\latest

   # 解压下载的文件到 C:\Android\sdk\cmdline-tools\latest

   # 设置环境变量（在系统环境变量中添加）
   ANDROID_HOME = C:\Android\sdk
   ANDROID_SDK_ROOT = C:\Android\sdk

   # 在 PATH 中添加
   %ANDROID_HOME%\cmdline-tools\latest\bin
   %ANDROID_HOME%\platform-tools
   %ANDROID_HOME%\emulator
   ```

3. **使用 sdkmanager 安装必要的组件**
   ```powershell
   # 接受许可协议
   sdkmanager --licenses

   # 安装必要的 SDK 组件
   sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
   sdkmanager "system-images;android-34;google_apis;x86_64"
   sdkmanager "emulator"
   ```

### 2. 安装 Java JDK

```powershell
# 使用 Chocolatey 安装（推荐）
choco install openjdk17

# 或者从 Oracle 官网下载安装
# https://www.oracle.com/java/technologies/downloads/#java17
```

### 3. 创建 Gradle Wrapper

```powershell
cd d:\03-Work\todo

# 如果系统中已安装 gradle
gradle wrapper

# 或者手动下载 gradle-wrapper.jar
# 从 https://services.gradle.org/distributions/ 下载
# 放到 gradle/wrapper/ 目录
```

### 4. 构建项目

```powershell
cd d:\03-Work\todo

# 清理构建
.\gradlew.bat clean

# 构建调试版本 APK
.\gradlew.bat assembleDebug

# 生成的 APK 位于：app\build\outputs\apk\debug\app-debug.apk
```

### 5. 安装 Android 模拟器

#### 使用命令行创建模拟器

```powershell
# 创建 AVD（Android Virtual Device）
avdmanager create avd -n test_device -k "system-images;android-34;google_apis;x86_64" -d "pixel_5"

# 启动模拟器
emulator -avd test_device
```

#### 或使用第三方模拟器（推荐）

1. **BlueStacks**（游戏模拟器，免费）
   - 下载：https://www.bluestacks.com/
   - 安装后可以直接运行 APK 文件

2. **NoxPlayer**（轻量级，免费）
   - 下载：https://www.bignox.com/
   - 支持多开和自定义配置

3. **Genymotion**（专业版，有免费版）
   - 下载：https://www.genymotion.com/
   - 需要注册账号

### 6. 安装应用到设备

```powershell
# 连接真实设备（需要开启 USB 调试）
adb devices

# 安装 APK
adb install app\build\outputs\apk\debug\app-debug.apk

# 启动应用
adb shell am start -n com.example.todo/.MainActivity
```

### 7. 查看日志

```powershell
# 查看应用日志
adb logcat | findstr "com.example.todo"

# 查看所有日志
adb logcat

# 清除日志
adb logcat -c
```

## 方案二：使用在线 IDE

### 1. Replit（推荐）

- 访问：https://replit.com/
- 搜索 "Android" 模板
- 可以在线编写、构建和运行 Android 应用
- 支持导入现有项目

### 2. Gitpod

- 访问：https://gitpod.io/
- 连接 GitHub 仓库
- 自动配置开发环境
- 支持预览 Android 应用

### 3. CodeSandbox

- 访问：https://codesandbox.io/
- 支持 React Native 项目
- 可以创建类似的应用

## 方案三：使用 Web 版本演示（最简单）

我已经为您创建了一个 Web 版本的演示，可以在浏览器中直接查看应用界面和功能。

访问：`todo-demo.html`（即将创建）

## 方案四：使用 VS Code + 插件

### 1. 安装 VS Code

- 下载：https://code.visualstudio.com/

### 2. 安装必要插件

- **Android iOS Emulator**
- **Flutter**（如果使用 Flutter）
- **Java Extension Pack**
- **Gradle for Java**

### 3. 配置环境

```powershell
# 安装 Android SDK（参考方案一）
# 配置 VS Code 的 settings.json
{
    "java.home": "C:\\Program Files\\Java\\jdk-17",
    "android.sdk.path": "C:\\Android\\sdk"
}
```

## 快速开始指南（最简单的方式）

### 使用 BlueStacks 模拟器

1. **下载并安装 BlueStacks**
   ```
   访问：https://www.bluestacks.com/
   下载并安装 BlueStacks 5
   ```

2. **构建 APK**
   ```powershell
   cd d:\03-Work\todo
   .\gradlew.bat assembleDebug
   ```

3. **安装到 BlueStacks**
   ```
   方法一：直接拖拽 APK 文件到 BlueStacks 窗口
   方法二：右键 APK 文件 -> 打开方式 -> BlueStacks
   ```

4. **运行应用**
   ```
   在 BlueStacks 中点击应用图标即可运行
   ```

## 常见问题解决

### 1. Gradle 构建失败

```powershell
# 清理并重新构建
.\gradlew.bat clean
.\gradlew.bat build --stacktrace

# 检查 Java 版本
java -version

# 确保使用 JDK 17
```

### 2. 模拟器启动失败

```powershell
# 检查虚拟化是否启用
systeminfo | findstr /C:"Virtualization"

# 在 BIOS 中启用 Intel VT-x 或 AMD-V

# 或使用 HAXM（Intel）或 Hyper-V（Windows）
```

### 3. ADB 连接失败

```powershell
# 重启 ADB 服务
adb kill-server
adb start-server

# 检查设备连接
adb devices

# 在设备上启用 USB 调试
# 设置 -> 开发者选项 -> USB 调试
```

### 4. 权限问题

```powershell
# 以管理员身份运行 PowerShell
# 或设置适当的文件权限
icacls "d:\03-Work\todo" /grant Everyone:(OI)(CI)F
```

## 推荐工具清单

### 必需工具
- ✅ JDK 17
- ✅ Android SDK 命令行工具
- ✅ Gradle Wrapper
- ✅ Android 模拟器（BlueStacks 或官方模拟器）

### 可选工具
- 📱 ADB（Android Debug Bridge）
- 🖥️ VS Code（代码编辑）
- 📊 Android Device Monitor（查看设备信息）

## 最简配置（只需 3 步）

### 步骤 1：安装 JDK
```powershell
choco install openjdk17
```

### 步骤 2：安装 BlueStacks
```
下载并安装：https://www.bluestacks.com/
```

### 步骤 3：构建并运行
```powershell
cd d:\03-Work\todo
.\gradlew.bat assembleDebug
# 然后双击 app\build\outputs\apk\debug\app-debug.apk
```

## 调试技巧

### 1. 查看实时日志
```powershell
adb logcat -c
adb logcat | findstr "com.example.todo"
```

### 2. 查看崩溃日志
```powershell
adb logcat -b crash
```

### 3. 导出日志到文件
```powershell
adb logcat > log.txt
```

### 4. 查看内存使用
```powershell
adb shell dumpsys meminfo com.example.todo
```

### 5. 查看进程信息
```powershell
adb shell ps | findstr "com.example.todo"
```

## 性能优化

### 1. 启用 Gradle 并行构建
```properties
# 在 gradle.properties 中添加
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.jvmargs=-Xmx2048m
```

### 2. 使用增量编译
```properties
org.gradle.configureondemand=true
```

### 3. 减少内存占用
```properties
org.gradle.daemon=true
org.gradle.workers.max=2
```

## 总结

**最简单的方式**：
1. 安装 JDK 17
2. 安装 BlueStacks 模拟器
3. 使用 `.\gradlew.bat assembleDebug` 构建 APK
4. 直接双击 APK 文件安装到 BlueStacks

**最专业的方式**：
1. 安装 Android SDK 命令行工具
2. 使用官方 Android 模拟器
3. 使用 ADB 进行调试
4. 使用 logcat 查看日志

选择适合您的方式即可！
