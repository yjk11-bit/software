// StudyFlow · app 模块
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

// 构建产物与中间文件放到 OneDrive 之外（Windows 本机）：
// OneDrive 同步会锁住 build/ 目录导致 mergeDebugResources 失败，且中文路径有隐患；
// CI（Linux）不满足条件，维持默认位置。
if (System.getProperty("os.name").startsWith("Windows")) {
    layout.buildDirectory.set(file("C:/Android/build/studyflow/app"))
}

android {
    namespace = "cn.csu.studyflow"
    compileSdk = 35

    defaultConfig {
        applicationId = "cn.csu.studyflow"
        minSdk = 26          // Android 8.0+，主流版本（覆盖绝大多数校园设备）
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material3.window.size)
    implementation(libs.androidx.navigation.compose)
    debugImplementation(libs.androidx.ui.tooling)
}
