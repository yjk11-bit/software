@echo off
rem StudyFlow 一条命令构建（Windows）：编译 Debug APK
cd /d %~dp0
call gradlew.bat assembleDebug
if errorlevel 1 (
  echo [FAIL] 构建失败
  exit /b 1
)
echo [OK] 产物: app\build\outputs\apk\debug\app-debug.apk
