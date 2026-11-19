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
- Android Studio（winget 安装）+ SDK 命令行工具（`C:\Android\sdk`，含 platforms;android-35 / build-tools;35.0.0 / platform-tools；放在纯英文路径，避免 AS 模拟器对中文路径的报错）
- Gradle 发行版走**腾讯镜像**（`gradle/wrapper/gradle-wrapper.properties`），依赖仓库走**阿里云镜像**（`settings.gradle.kts`），国内网络可直接构建

## 三、如何构建（一条命令）

```bash
# 方式一（Windows，推荐）：一键脚本
android\build.cmd                # 只构建
android\build-and-run.cmd        # 构建 + 装进模拟器并启动（需 AVD 已启动）

# 方式二：命令行
cd android
./gradlew assembleDebug          # 产物见下
```

- **产物路径**：Windows 本机为 `C:\Android\build\studyflow\app\outputs\apk\debug\app-debug.apk`（构建目录已移出 OneDrive，避免同步锁与中文路径问题）；CI/其他平台为 `app/build/outputs/apk/debug/app-debug.apk`。
- 首次构建需下载依赖（几分钟），之后增量秒级。
- 若换机器：SDK 位置写进 `local.properties`（`sdk.dir=你的SDK路径`，该文件已 gitignore）；命令行构建需 JDK 17。
- 模拟器 AVD 目录：本机已设环境变量 `ANDROID_AVD_HOME=C:\Android\.android`（Windows 用户名含中文时必需，否则模拟器起不来）。

## 四、CI（提交即构建 + 静态检查）

工作流 [.github/workflows/android-ci.yml](../.github/workflows/android-ci.yml)：`android/**` 变更触发 push / pull_request 时，在 GitHub Actions 上跑 **编译 + Lint**，失败即阻断，APK 作为 artifact 上传。对应 W2 执行手册 4.1「CI 跑通有记录」。

## 五、模拟器启动

1. Android Studio → Device Manager → 建一个 Pixel 级 AVD（API 35 镜像）
2. 点 Run，或命令行 `./gradlew installDebug`（AVD 已启动时）
3. 应看到 4 页 UI 骨架：课程首页 / 章节导航 / 资料库+错题本 / 复习+计划

## 六、范围与边界

- W2 交付：4 页 UI 骨架承载课程模块 6 入口（`ui/` 下：课程首页 / 章节导航 / 资料库+错题本 / 复习+计划），手机单栏 ↔ 平板双栏（WindowSizeClass）。
- `data/model` 按 A 的 3.3 数据契约建模（六标签 + 错题八字段 + 知识点关联），`data/mock` 为占位样例（文学院/计科两场景，等 D 的场景样例灌入）。
- `bridge/` 留空待接（对应 demo 的 adapters.js，边界约定见仓库根《界面层API对接清单_赛题1.md》）；A 换真实实现时界面代码不改。
- 课程模块按「分两步」：本周只做第一步（单课三级组织 + 6 入口），关联模块（第二步）只留占位。
- Git：代码按模块走 **feature 分支**（如 `feature/android-scaffold`），文档走 master；提交带规范 message。
