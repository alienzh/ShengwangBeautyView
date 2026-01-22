# Shengwang Beauty Control View

声网美颜控制组件，提供完整的美颜功能集成方案。支持 AAR 和源码两种集成方式。

## 🚀 快速开始

### 方式一：使用 AAR

#### 1. 生成 AAR 文件

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
    
    // Agora RTC SDK（必需）
    implementation 'io.agora.rtc:agora-special-full:4.5.2.8'
}
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

## 📁 lib 项目结构

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
└── └── src/main/res/             # 所有资源文件
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