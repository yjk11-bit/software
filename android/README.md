# StudyFlow 智学伴侣 · Android 工程（W1 空壳）

> 第二十届全国大学生软件创新大赛 · 赛题1 ｜ 负责人：B（CS 移动前端+UX）
> 对应《第一周（W1）工作安排·执行手册》4.1：App 脚手架可编译运行。

## 一、工程信息

| 项 | 值 |
|---|---|
| 包名 / applicationId | `cn.csu.studyflow` |
| 工程名 | StudyFlow |
| 语言 / UI | Kotlin 2.1.20 + Jetpack Compose（Material3） |
| 构建 | Gradle 8.11.1 + Android Gradle Plugin 8.9.2（版本目录 `gradle/libs.versions.toml`） |
| JDK | 17（Temurin） |
| minSdk / targetSdk / compileSdk | 26（Android 8.0+）/ 35 / 35 |
| 目录骨架 | `ui/`（页面）· `data/`（本地模型）· `bridge/`（demo adapters.js 五个能力的原生实现，W3 后接）· `res/`（资源） |

## 二、环境版本（本周已在本机验证）

- Windows 11 + Temurin JDK 17（`JAVA_HOME=D:\Scoop\apps\temurin17-jdk\current`）
- Android Studio（winget 安装）+ SDK 命令行工具（`~/tools/android-sdk`，含 platforms;android-35 / build-tools;35.0.0 / platform-tools）
- Gradle 发行版走**腾讯镜像**（`gradle/wrapper/gradle-wrapper.properties`），依赖仓库走**阿里云镜像**（`settings.gradle.kts`），国内网络可直接构建

## 三、如何构建

```bash
# 方式一：命令行（本机已验证）
cd android
./gradlew assembleDebug          # 产物：app/build/outputs/apk/debug/app-debug.apk

# 方式二：Android Studio
# File → Open 选本目录 → 等 Gradle Sync 完成 → 点 Run
```

- 首次构建需下载依赖（几分钟），之后增量秒级。
- 若换机器：SDK 位置写进 `local.properties`（`sdk.dir=你的SDK路径`，该文件已 gitignore）；命令行构建需 JDK 17。

## 四、模拟器启动

1. Android Studio → Device Manager → 建一个 Pixel 级 AVD（API 35 镜像）
2. 点 Run 或命令行 `./gradlew installDebug`（AVD 已启动时）
3. 应看到「StudyFlow 智学伴侣 · W1 Android 空壳」页面

## 五、本周范围与边界

- W1 只验收：**可编译、模拟器能跑 Hello World、目录与构建说明入库**，不要求业务界面。
- 业务页面 W2 起在 `ui/` 下落地；`bridge/` 与 `data/` 留空待接（对应 demo 的 adapters.js / data.js，边界约定见仓库根《界面层API对接清单_赛题1.md》）。
- Git：代码按模块走 **feature 分支**（如 `feature/android-scaffold`），文档走 master；提交带规范 message。
