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

- ✅ **4大美颜模块**：美颜（美肤+美型+画质）、风格妆、滤镜、贴纸
- ✅ **60+ 美颜参数**：涵盖美肤（9项）、美型（32项）、画质（4项）全方位调节
- ✅ **对比功能**：支持美颜效果对比（开关切换模式）
- ✅ **重置功能**：一键恢复默认参数（美颜模块支持）
- ✅ **参数保存**：支持保存美颜参数到本地
- ✅ **10+ 风格妆模板**：学妹妆、学姐妆、气质妆、白皙妆、优雅妆等
- ✅ **40+ 滤镜模板**：暖色系、冷/白色系、氛围系、环境系
- ✅ **12+ 贴纸素材**：圣诞节、章鱼、蝴蝶、粉刷时光等
- ✅ **自动刷新**：美颜状态变化时自动刷新 UI
- ✅ **生命周期管理**：自动管理 View 生命周期
- ✅ **类型化 API**：使用枚举和类型别名，API 更安全

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

// 4. 刷新页面列表（当配置变化时，通常不需要手动调用，View 会自动刷新）
// beautyView.refreshPageList()

// 5. 重置/保存美颜参数（可选）
// beautyView.resetBeauty()  // 重置为默认值
// beautyView.saveBeauty()   // 保存当前参数
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

## 📦 生成 AAR

### 快速生成

在项目根目录运行打包脚本：

```bash
./lib/build-aar.sh
```

### 版本管理

修改 `lib/build-aar.sh` 脚本中的 `VERSION_NAME` 变量来设置版本号：

```bash
VERSION_NAME="1.0.0"  # 修改为你想要的版本号
```

版本号格式：`主版本.次版本.修订版本`（例如：`1.0.0`、`1.1.0`、`2.0.0`）

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
8. **生命周期管理**：`ShengwangBeautyView` 会自动管理生命周期，无需手动处理
9. **API 变更**：
   - `makeupStrength` 已更名为 `makeupIntensity`
   - `showBackground` 属性已移除，改用 `BeautyItemType` 枚举来控制 UI 显示
   - `onPageListCreate()` 和 `onSelectedChanged()` 不再是 `open`，如需自定义请继承类

## 🐛 常见问题

### Q: AAR 文件很大？
A: AAR 包含了所有资源文件（图标、布局等），这是正常的。如果客户需要自定义资源，可以自行修改代码资源。

### Q: 找不到资源文件？
A: 确保 AAR 正确添加到依赖中，并且项目已同步。可以检查 `app/build/intermediates/merged_res` 目录。

### Q: 编译错误：找不到类？
A: 检查是否添加了所有必需的依赖（AndroidX、Material Design 等）。

### Q: 编译错误：找不到 RTC SDK？
A: AAR 中 RTC SDK 为 `compileOnly`，客户需要在自己的项目中添加 RTC SDK 依赖。

### Q: 如何重置美颜参数？
A: 调用 `beautyView.resetBeauty()` 方法，可以传入 `BeautyModule` 参数来重置指定模块。

### Q: 如何保存美颜参数？
A: 调用 `beautyView.saveBeauty()` 方法，可以传入 `BeautyModule` 参数来保存指定模块的参数。保存的配置会写入到美颜资源目录中。

### Q: 为什么保存的美颜配置丢失了？
A: **最常见原因**：业务层每次启动都复制资源文件，覆盖了已保存的配置。解决方案：使用 SharedPreferences 等机制记录资源是否已复制，避免重复复制。详见"保存美颜参数"章节的说明。

### Q: 如何更新美颜资源文件？
A: 如果需要更新资源文件，需要清除应用的 SharedPreferences 数据（移除 `material_copied` 标记）或卸载重装应用，然后重新复制资源。注意：更新资源可能会影响已保存的美颜配置（如果配置格式不兼容）。

### Q: 如何自定义页面列表？
A: 继承 `ShengwangBeautyView` 类，重写 `onPageListCreate()` 方法来自定义页面列表。

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
│   │   │   │   └── BeautyPageInfo.kt  # 包含 BeautyPageInfo, BeautyItemInfo, BeautyItemType, BeautyModule
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

## 🔧 API 使用说明

### 主要 API

#### ShengwangBeautyView

**刷新页面列表**
```kotlin
beautyView.refreshPageList()
```
当美颜配置发生变化时，调用此方法刷新 UI 显示。

**重置美颜参数**
```kotlin
// 重置美颜模块（默认）
beautyView.resetBeauty()

// 重置指定模块
beautyView.resetBeauty(BeautyModule.FILTER)      // 重置滤镜
beautyView.resetBeauty(BeautyModule.STYLE_MAKEUP) // 重置美妆
beautyView.resetBeauty(BeautyModule.STICKER)      // 重置贴纸
```
重置操作会将参数恢复为出厂时模板内的默认值。注意：重置后，下次 `addOrUpdate` 加载节点时会自动生效。

**保存美颜参数**
```kotlin
// 保存美颜模块（默认）
beautyView.saveBeauty()

// 保存指定模块
beautyView.saveBeauty(BeautyModule.FILTER)
beautyView.saveBeauty(BeautyModule.STYLE_MAKEUP)
beautyView.saveBeauty(BeautyModule.STICKER)
```

保存操作会将当前调整的参数保存到本地，下次 `addOrUpdate` 加载节点时会自动调用之前保存的参数。

**⚠️ 重要提示：资源复制与配置保存**

保存的美颜配置会写入到美颜资源目录中。**业务层必须避免每次启动都复制资源文件**，否则会覆盖已保存的美颜配置。

**正确做法：**
```kotlin
// ✅ 推荐：只在首次或资源更新时复制
private fun initializeBeautyResources(context: Context) {
    val storagePath = context.getExternalFilesDir("")?.absolutePath ?: return
    val destPath = "$storagePath/beauty_agora"
    
    // 检查资源是否已复制（使用 SharedPreferences 或其他方式记录）
    if (!isMaterialCopied(context)) {
        // 首次复制资源
        copyAssets(context, "beauty_agora", destPath)
        setMaterialCopied(context, true)
    } else {
        // 资源已存在，不重复复制
        // 这样可以保留之前保存的美颜配置
    }
    
    val materialPath = "$destPath/beauty_material_functional"
    ShengwangBeautySDK.initBeautySDK(materialPath, rtcEngine)
}
```

**错误做法：**
```kotlin
// ❌ 错误：每次启动都复制资源，会覆盖保存的配置
private fun initializeBeautyResources(context: Context) {
    val storagePath = context.getExternalFilesDir("")?.absolutePath ?: return
    val destPath = "$storagePath/beauty_agora"
    
    // 每次都复制，会覆盖已保存的美颜配置！
    copyAssets(context, "beauty_agora", destPath)
    
    val materialPath = "$destPath/beauty_material_functional"
    ShengwangBeautySDK.initBeautySDK(materialPath, rtcEngine)
}
```

**资源更新场景：**
如果需要更新美颜资源文件（例如升级 SDK 版本），需要：
1. 清除应用的 SharedPreferences 数据（移除 `material_copied` 标记）
2. 或者卸载重装应用
3. 然后重新复制资源文件

这样可以确保资源文件更新，同时不影响已保存的美颜配置（如果配置格式兼容）。

#### BeautyModule（模块类型）

```kotlin
typealias BeautyModule = IVideoEffectObject.VIDEO_EFFECT_NODE_ID

// 可用值：
BeautyModule.BEAUTY          // 美颜模块（美肤+美型+画质）
BeautyModule.STYLE_MAKEUP     // 风格妆模块
BeautyModule.FILTER           // 滤镜模块
BeautyModule.STICKER          // 贴纸模块
```

#### BeautyItemType（功能项类型）

```kotlin
enum class BeautyItemType {
    NORMAL,  // 普通参数项（默认）
    TOGGLE,  // 开关项（如美颜总开关）
    RESET,   // 重置项
    NONE     // 无效果项（如取消贴纸/美妆）
}
```

### 生命周期管理

`ShengwangBeautyView` 会自动管理生命周期：
- 在 `onAttachedToWindow()` 时注册状态监听器
- 在 `onDetachedFromWindow()` 时移除监听器
- 当美颜状态变化时，会自动刷新页面列表

### 自定义配置

如果需要自定义美颜功能，可以：

1. **修改页面列表**：重写 `ShengwangBeautyView.onPageListCreate()` 方法
2. **添加新功能项**：在对应的 PageBuilder 中添加新的 `BeautyItemInfo`
3. **自定义资源**：修改 `lib/src/main/res/` 下的资源文件
4. **自定义选中回调**：重写 `ShengwangBeautyView.onSelectedChanged()` 方法

**注意**：`onPageListCreate()` 和 `onSelectedChanged()` 方法已改为 `protected`（不再是 `open`），如需自定义请继承 `ShengwangBeautyView` 类。

## 📞 技术支持

如有问题，请联系技术支持团队。
