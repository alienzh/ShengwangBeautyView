import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

// Load local.properties file for Agora configuration
val properties = Properties()
val propertiesFile = rootProject.file("local.properties")
if (propertiesFile.exists()) {
    propertiesFile.inputStream().use { properties.load(it) }
}

android {
    namespace = "cn.shengwang.beauty.demo"
    compileSdk {
        version = release(35)
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    defaultConfig {
        applicationId = "cn.shengwang.beauty.demo"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "Shengwang_App_ID", "\"${properties.getProperty("shengwang.appId", "")}\"")
    }

    signingConfigs {
        maybeCreate("release").apply {
            storeFile = file(rootProject.rootDir.absolutePath + "/keystore.key")
            storePassword = "965606"
            keyAlias = "agora"
            keyPassword = "965606"
        }
    }

    applicationVariants.all {
        val buildTimestamp = System.currentTimeMillis()
        outputs.all {
            val output = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            output.outputFileName = "ShengwangBeautyView_v${versionName}_${buildTimestamp}.apk"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // 依赖 lib 模块（美颜核心代码和资源）
    // 方式1：使用源码模块
    implementation(project(":lib"))

    // 方式2：使用 AAR 文件
    // 使用 release 目录下的 AAR 文件
//    implementation(files("../release/shengwang-beauty-view-1.0.0.aar"))

    // AndroidX 依赖
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)

    // Agora RTC SDK（示例应用需要）
    implementation(libs.rtc.special)

    // OkHttp for network requests
    implementation(libs.okhttp.core)

    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // 测试依赖
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
