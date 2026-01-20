# Shengwang Beauty Control View

声网美颜控制组件，提供完整的美颜功能集成方案。支持 AAR 和源码两种集成方式。

## 📦 提供方式

本项目提供 **AAR 格式**的库文件，包含所有 UI 组件和资源文件，方便客户快速集成。

**为什么使用 AAR？**
- ✅ 包含 30+ 图标资源，无需手动处理
- ✅ 包含所有布局和字符串资源
- ✅ 一键集成，降低集成成本
- ✅ 避免资源冲突和路径问题
- ✅ 更好的版本管理

## ✨ 功能特性

- ✅ 美颜功能（磨皮、美白、红润、瘦脸、大眼等）
- ✅ 画质调整（色调、色温、饱和度、亮度）
- ✅ 滤镜效果（40+ 滤镜模板）
- ✅ 美妆效果（10+ 风格妆模板）
- ✅ 贴纸效果（12+ 贴纸素材）
- ✅ 本地资源支持

## 🚀 快速开始

### 方式一：使用 AAR（推荐）

#### 1. 获取 AAR 文件

AAR 文件位于 `release/` 目录下，文件名格式：`shengwang-beauty-view-{version}.aar`

#### 2. 集成到项目

1. 将 AAR 文件复制到项目的 `libs` 目录（如 `app/libs/`）

2. 在 `app/build.gradle` 中添加：

```gradle
android {
    ...
    repositories {
        flatDir {
            dirs 'libs'
        }
    }
}

dependencies {
    // 添加 AAR 依赖
    implementation(name: 'shengwang-beauty-view-1.0.0', ext: 'aar')
    
    // 必需依赖（如果项目中还没有）
    implementation 'androidx.core:core-ktx:1.17.0'
    implementation 'androidx.appcompat:appcompat:1.7.1'
    implementation 'com.google.android.material:material:1.13.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.2.0'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0'
    
    // Agora RTC SDK（必需）
    implementation 'io.agora.rtc:agora-special-full:4.5.2.8'
}
```

#### 3. 必需配置

**美颜资源文件**

**重要：** 客户需要自己准备 Agora 美颜资源文件，并放置在应用的 `assets/beauty_agora/` 目录下。

资源文件结构：
```
app/src/main/assets/beauty_agora/
└── beauty_material_functional/
    └── ... (美颜资源文件)
```

**权限配置**

**重要：** AAR 库中不包含权限声明，客户需要在自己的应用模块中声明权限。

在客户应用的 `AndroidManifest.xml` 中添加：

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```

**说明：** 权限必须在客户的应用模块中声明，而不是在库模块中。这样可以避免权限冲突，并让客户有更好的控制权。

**ProGuard 规则（如果启用混淆）**

如果项目启用了代码混淆，需要在 `proguard-rules.pro` 中添加：

```proguard
# Shengwang Beauty Control View
-keep public class cn.shengwang.beauty.** { *; }
-keep class cn.shengwang.beauty.databinding.** { *; }
-keep class cn.shengwang.beauty.R$* { *; }
```

#### 4. 在代码中使用

```kotlin

// 1. 初始化 Agora RTC Engine（客户自己实现）
val rtcEngine = RtcEngine.create(context, appId, rtcEventHandler)

// 2. 初始化美颜 SDK
// materialPath 是美颜资源文件的路径，例如："/sdcard/Android/data/your.package.name/files/beauty_agora/beauty_material_functional"
val materialPath = context.getExternalFilesDir("")?.absolutePath + "/beauty_agora/beauty_material_functional"
ShengwangBeautySDK.initBeautySDK(materialPath, rtcEngine)

// 3. 在布局中添加 ShengwangBeautyView
// XML 方式：
// <cn.shengwang.beauty.ui.ShengwangBeautyView
//     android:id="@+id/beautyControlView"
//     android:layout_width="match_parent"
//     android:layout_height="wrap_content" />

// 代码方式：
val beautyView = ShengwangBeautyView(context).apply {
    layoutParams = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.WRAP_CONTENT
    )
}
container.addView(beautyView)

// 4. 刷新页面列表（当配置变化时）
beautyView.refreshPageList()
```

### 方式二：源码集成（直接拷贝 lib 模块）

如果需要源码集成，可以直接拷贝整个 `lib` 模块到客户项目中：

1. **拷贝 lib 模块**：将整个 `lib` 目录复制到客户项目中

2. **在 `settings.gradle.kts` 中添加模块**：

```kotlin
include(":lib")
```

3. **在 `app/build.gradle` 中添加依赖**：

```gradle
dependencies {
    // 依赖 lib 模块
    implementation(project(":lib"))
    
    // Agora RTC SDK（必需）
    implementation 'io.agora.rtc:agora-special-full:4.5.2.8'
    
    // 其他依赖...
}
```

**注意**：拷贝 lib 模块后，需要确保：
- lib 模块的 `build.gradle.kts` 配置正确
- 命名空间为 `cn.shengwang.beauty`
- 所有资源文件都已包含

## 📦 打包 AAR

如果需要重新打包 AAR，运行：

```bash
# 方式一：使用脚本（推荐）
# 在项目根目录运行
./lib/build-aar.sh

# 方式二：使用 Gradle 命令
./gradlew :lib:assembleRelease
```

打包后的 AAR 文件位于：`lib/build/outputs/aar/lib-release.aar`

脚本会自动将 AAR 复制到 `release/` 目录。

### 版本管理

建议版本号格式：`主版本.次版本.修订版本`

例如：`1.0.0`、`1.1.0`、`2.0.0`

在 `lib/build.gradle.kts` 中修改版本号（注意：library 模块不支持 versionCode 和 versionName，版本信息在 AAR 文件名中体现）。

## 📋 AAR 文件内容

打包的 AAR 包含：

- ✅ **所有 Java/Kotlin 源码**（编译后的 .class 文件）
- ✅ **所有资源文件**：
  - drawable（图标、背景等）
  - layout（布局文件）
  - values（字符串、颜色、主题等）
- ✅ **AndroidManifest.xml**
- ✅ **ProGuard 规则**
- ✅ **R.txt**（资源映射文件）

### 当前资源统计

- **图标文件**：30+ 个 PNG 图标
- **布局文件**：4 个 XML 布局
- **字符串资源**：100+ 个字符串
- **其他资源**：颜色、主题、背景等

如果提供源码，客户需要手动处理所有这些文件。

## 📝 注意事项

1. **资源文件**：AAR 中包含所有 UI 资源（图标、布局、字符串等），客户无需手动添加
2. **版本兼容**：确保使用的 Agora RTC SDK 版本与 AAR 兼容
3. **Kotlin 版本**：项目使用 Kotlin 2.0.21，确保客户项目兼容
4. **最小 SDK**：AAR 要求 minSdk = 26（Android 8.0）
5. **RTC SDK 依赖**：AAR 中 RTC SDK 为 `compileOnly`，客户需要自己添加依赖
6. **AAR 文件大小**：由于包含所有资源，AAR 文件可能较大（通常 1-5MB），这是正常的
7. **资源冲突**：如果客户项目中有同名资源，可能会冲突，建议使用资源前缀

## 🐛 常见问题

### Q: AAR 文件很大？
A: AAR 包含了所有资源文件（图标、布局等），这是正常的。如果客户需要自定义资源，可以自行修改代码资源。

### Q: 找不到资源文件？
A: 确保 AAR 正确添加到依赖中，并且项目已同步。可以检查 `app/build/intermediates/merged_res` 目录。

### Q: 编译错误：找不到类？
A: 检查是否添加了所有必需的依赖（AndroidX、Material Design 等）。

### Q: 编译错误：找不到 RTC SDK？
A: AAR 中 RTC SDK 为 `compileOnly`，客户需要在自己的项目中添加 RTC SDK 依赖。

## 📁 项目结构

```
项目根目录/
├── lib/                          # 库模块（核心代码和资源）
│   ├── src/main/java/cn/shengwang/beauty/
│   │   ├── core/                 # 核心 SDK 封装
│   │   │   ├── ShengwangBeautySDK.kt
│   │   │   └── BeautyParameter.kt
│   │   ├── ui/                   # UI 组件
│   │   │   ├── ShengwangBeautyView.kt
│   │   │   ├── model/            # 数据模型
│   │   │   │   └── BeautyPageInfo.kt
│   │   │   ├── contract/         # 接口定义
│   │   │   │   └── IPageBuilder.kt
│   │   │   └── builder/          # 页面构建器
│   │   │       ├── BeautyPageBuilder.kt
│   │   │       ├── FilterPageBuilder.kt
│   │   │       ├── MakeupPageBuilder.kt
│   │   │       └── StickerPageBuilder.kt
│   └── src/main/res/             # 所有资源文件
│       ├── drawable/
│       ├── layout/
│       └── values/
├── app/                          # 示例应用模块
│   ├── src/main/java/cn/shengwang/beauty/
│   │   ├── BeautyExampleActivity.kt
│   │   └── BeautyManager.kt
│   └── src/main/assets/
│       └── beauty_agora/         # 美颜资源目录（需要客户自己放置）
└── release/                      # AAR 发布目录
    └── shengwang-beauty-view-{version}.aar
```

## 🔧 自定义配置

如果需要自定义美颜功能，可以：

1. **修改页面列表**：在 `ShengwangBeautyView.onPageListCreate()` 方法中修改
2. **添加新功能项**：在对应的 PageBuilder 中添加新的 `BeautyItemInfo`
3. **自定义资源**：修改 `lib/src/main/res/` 下的资源文件

## 📞 技术支持

如有问题，请联系技术支持团队。
