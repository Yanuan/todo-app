@echo off
chcp 65001 >nul
echo ========================================
echo 待办事项提醒应用 - 快速启动脚本
echo ========================================
echo.

REM 检查 Java 是否安装
echo [1/5] 检查 Java 环境...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 未检测到 Java，请先安装 JDK 17
    echo 下载地址: https://adoptium.net/
    pause
    exit /b 1
)
echo ✅ Java 环境正常
echo.

REM 检查 Gradle Wrapper
echo [2/5] 检查 Gradle Wrapper...
if not exist "gradlew.bat" (
    echo ❌ 未找到 gradlew.bat
    pause
    exit /b 1
)
echo ✅ Gradle Wrapper 存在
echo.

REM 清理之前的构建
echo [3/5] 清理之前的构建...
call gradlew.bat clean
if %errorlevel% neq 0 (
    echo ⚠️ 清理失败，继续构建...
)
echo ✅ 清理完成
echo.

REM 构建调试版本
echo [4/5] 构建调试版本 APK...
echo 这可能需要几分钟，请耐心等待...
call gradlew.bat assembleDebug
if %errorlevel% neq 0 (
    echo ❌ 构建失败
    echo 请检查错误信息并重试
    pause
    exit /b 1
)
echo ✅ 构建成功
echo.

REM 显示 APK 位置
echo [5/5] 构建完成！
echo.
echo ========================================
echo 📱 APK 文件位置:
echo %cd%\app\build\outputs\apk\debug\app-debug.apk
echo ========================================
echo.

REM 检查 APK 是否存在
if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo ✅ APK 文件已生成
    echo.
    echo 安装方法:
    echo 1. 将 APK 文件复制到 Android 设备
    echo 2. 在设备上点击 APK 文件进行安装
    echo 3. 或使用 adb install 命令安装
    echo.
    echo 使用 ADB 安装:
    echo adb install app\build\outputs\apk\debug\app-debug.apk
    echo.
    
    REM 询问是否打开文件夹
    set /p open_folder="是否打开 APK 所在文件夹? (Y/N): "
    if /i "%open_folder%"=="Y" (
        explorer "app\build\outputs\apk\debug"
    )
) else (
    echo ❌ APK 文件未找到
    echo 请检查构建过程
)

echo.
echo 按任意键退出...
pause >nul
