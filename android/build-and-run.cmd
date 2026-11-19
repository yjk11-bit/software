@echo off
rem StudyFlow 一条命令构建 + 装进模拟器并启动（需先启动 AVD，且 adb 在 PATH 中）
cd /d %~dp0
call gradlew.bat assembleDebug
if errorlevel 1 (
  echo [FAIL] 构建失败
  exit /b 1
)
adb install -r app\build\outputs\apk\debug\app-debug.apk
if errorlevel 1 (
  echo [FAIL] 安装失败，请确认模拟器已启动（adb devices 应看到 emulator-5554）
  exit /b 1
)
adb shell am start -n cn.csu.studyflow/.MainActivity
echo [OK] StudyFlow 已在设备上启动
