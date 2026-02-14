# 🚀 快速开始指南

## 📱 查看应用演示（推荐，最简单）

### 方法一：直接在浏览器中查看

1. **打开演示页面**
   - 双击文件：`todo-demo.html`
   - 或在浏览器中打开：`file:///d:/03-Work/todo/todo-demo.html`

2. **功能演示**
   - ✅ 查看待办事项列表
   - ➕ 添加新的待办事项
   - ✏️ 编辑现有待办事项
   - 🗑️ 删除待办事项
   - ☑️ 启用/禁用待办事项
   - 📱 模拟手机界面

### 方法二：使用在线预览

将 `todo-demo.html` 文件上传到任意静态网站托管服务，如：
- GitHub Pages
- Netlify
- Vercel

## 🔧 构建真实 Android 应用

### 前置要求

1. **安装 JDK 17**
   - 下载：https://adoptium.net/
   - 选择 Temurin 17 (LTS) 版本
   - 安装后验证：`java -version`

2. **安装 Android 模拟器（推荐 BlueStacks）**
   - 下载：https://www.bluestacks.com/
   - 安装后可以直接运行 APK 文件

### 快速构建（3 步）

#### 步骤 1：双击运行构建脚本
```
双击文件：build.bat
```

#### 步骤 2：等待构建完成
- 脚本会自动清理、构建 APK
- 大约需要 2-5 分钟
- 构建成功后会显示 APK 位置

#### 步骤 3：安装到模拟器
```
方法一：直接双击 APK 文件
方法二：拖拽 APK 到 BlueStacks 窗口
```

## 📋 手动构建步骤

如果您想手动控制构建过程：

### 1. 清理之前的构建
```powershell
.\gradlew.bat clean
```

### 2. 构建调试版本
```powershell
.\gradlew.bat assembleDebug
```

### 3. 查找 APK 文件
```
位置：app\build\outputs\apk\debug\app-debug.apk
```

### 4. 安装到设备

#### 使用 ADB（需要连接 Android 设备）
```powershell
# 连接设备（需要开启 USB 调试）
adb devices

# 安装 APK
adb install app\build\outputs\apk\debug\app-debug.apk

# 启动应用
adb shell am start -n com.example.todo/.MainActivity
```

#### 使用模拟器（BlueStacks）
```
1. 打开 BlueStacks
2. 双击 APK 文件
3. 等待安装完成
4. 在 BlueStacks 中点击应用图标运行
```

## 🎯 应用功能说明

### 主界面
- 显示所有待办事项列表
- 每个卡片显示标题、描述、提醒时间和重复周期
- 支持快速启用/禁用
- 浮动按钮添加新待办事项

### 添加待办事项
1. 点击右下角的 `+` 按钮
2. 输入标题（必填）
3. 可选输入描述
4. 选择提醒时间
5. 选择重复周期（仅一次、每天、每周、每月）
6. 点击保存

### 编辑待办事项
1. 点击待办事项卡片上的编辑图标
2. 修改需要更新的字段
3. 点击保存

### 删除待办事项
1. 点击待办事项卡片上的删除图标
2. 在确认对话框中点击删除

### 启用/禁用待办事项
1. 点击待办事项卡片左侧的复选框
2. 取消选中表示禁用，选中表示启用

## 🔍 查看日志

### 使用 ADB 查看应用日志
```powershell
# 查看应用日志
adb logcat | findstr "com.example.todo"

# 查看所有日志
adb logcat

# 清除日志
adb logcat -c
```

## 📚 相关文档

- **[README.md](README.md)** - 项目概述和详细说明
- **[FEATURES.md](FEATURES.md)** - 功能详细说明和界面展示
- **[CODE_EXAMPLES.md](CODE_EXAMPLES.md)** - 核心代码示例
- **[RUN_WITHOUT_ANDROID_STUDIO.md](RUN_WITHOUT_ANDROID_STUDIO.md)** - 无 Android Studio 运行指南

## 🛠️ 常见问题

### Q: 构建失败怎么办？
A: 
1. 检查 Java 版本是否为 17
2. 确保网络连接正常（需要下载依赖）
3. 尝试运行 `.\gradlew.bat clean` 后重新构建

### Q: 模拟器无法安装 APK？
A:
1. 确保模拟器已启动
2. 尝试重启模拟器
3. 检查 APK 文件是否完整

### Q: 应用无法接收通知？
A:
1. 检查应用通知权限
2. 确保待办事项已启用
3. 检查系统设置中的闹钟权限

### Q: 如何调试应用？
A:
1. 使用 ADB 连接设备
2. 运行 `adb logcat` 查看日志
3. 查看应用崩溃信息

## 💡 提示

### 推荐工作流程

1. **开发阶段**
   - 使用 `todo-demo.html` 快速查看界面效果
   - 修改代码后重新构建

2. **测试阶段**
   - 使用 BlueStacks 模拟器测试
   - 使用真实设备测试通知功能

3. **发布阶段**
   - 构建发布版本：`.\gradlew.bat assembleRelease`
   - 签名 APK
   - 上传到应用商店

### 性能优化

- 首次构建会下载依赖，后续构建会更快
- 使用 `.\gradlew.bat assembleDebug --offline` 离线构建
- 增加 Gradle 内存：修改 `gradle.properties` 中的 `org.gradle.jvmargs`

## 🎉 开始使用

### 最简单的方式（3 步）

1. **双击** `todo-demo.html` 查看演示
2. **双击** `build.bat` 构建 APK
3. **双击** 生成的 APK 文件安装到模拟器

就这么简单！🎊

---

## 📞 需要帮助？

如果遇到问题，请查看：
- [RUN_WITHOUT_ANDROID_STUDIO.md](RUN_WITHOUT_ANDROID_STUDIO.md) - 详细的故障排除指南
- [README.md](README.md) - 项目技术文档
- [FEATURES.md](FEATURES.md) - 功能说明文档

祝您使用愉快！🚀
