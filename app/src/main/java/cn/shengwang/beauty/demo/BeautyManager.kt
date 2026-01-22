package cn.shengwang.beauty.demo

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import cn.shengwang.beauty.core.ShengwangBeautySDK
import io.agora.rtc2.Constants
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineEx
import io.agora.rtc2.video.VideoCanvas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.lang.ref.WeakReference

object BeautyManager {

    private const val TAG = "BeautyManager"
    private const val PREFS_NAME = "agora_beauty_sdk_prefs"
    private const val KEY_MATERIAL_COPIED = "material_copied"

    private var context: Application? = null
    private var rtcEngine: RtcEngine? = null

    private var videoView: WeakReference<View>? = null
    private var renderMode: Int = Constants.RENDER_MODE_HIDDEN

    // Use CoroutineScope instead of Executor
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Track initialization status
    private var agoraInitSuccess = false

    // Beauty switch
    var enable = false
        set(value) {
            field = value
            ShengwangBeautySDK.enable(value)
        }

    fun initialize(context: Context, rtcEngine: RtcEngine) {
        this.context = context.applicationContext as Application
        this.rtcEngine = rtcEngine
        this.enable = true
        // Initialize Beauty SDK using coroutines
        createBeauty()
    }

    fun setupLocalVideo(view: View, renderMode: Int) {
        // Use Dispatchers.Main instead of Handler
        scope.launch(Dispatchers.Main) {
            this@BeautyManager.videoView = WeakReference(view)
            this@BeautyManager.renderMode = renderMode
            rtcEngine?.setupLocalVideo(VideoCanvas(view, renderMode, 0))
        }
    }

    fun destroy() {
        rtcEngine?.registerVideoFrameObserver(null)
        scope.launch(Dispatchers.Main) {
            videoView?.get()?.let {
                rtcEngine?.setupLocalVideo(VideoCanvas(null))
            }
            videoView = null
        }
        destroyBeauty()
        context = null
        rtcEngine = null
        RtcEngineEx.destroy()
    }

    private fun createBeauty() =
        scope.launch {
            val ctx = context ?: return@launch
            val rtc = rtcEngine ?: return@launch

            // Execute initialization on IO dispatcher
            withContext(Dispatchers.IO) {
                val storagePath = ctx.getExternalFilesDir("")?.absolutePath ?: return@withContext
                if (!isMaterialCopied(ctx)) {
                    val destPath = "$storagePath/beauty_agora"
                    copyAssets(ctx, "beauty_agora", destPath)
                    setMaterialCopied(ctx, true)
                    Log.d(TAG, "Beauty materials copied to $destPath")
                } else {
                    Log.d(TAG, "Beauty materials already copied, skipping copy operation")
                }
                val materialPath = "$storagePath/beauty_agora/beauty_material_functional"

                agoraInitSuccess = ShengwangBeautySDK.initBeautySDK(materialPath, rtc)
                ShengwangBeautySDK.enable(enable)
            }

            // Delay and setup video on main thread
            delay(140)
            withContext(Dispatchers.Main) {
                videoView?.get()?.let {
                    rtcEngine?.setupLocalVideo(
                        VideoCanvas(it, renderMode, 0)
                    )
                }
            }
        }

    private fun destroyBeauty() =
        scope.launch {
            // Clear video view on main thread
            withContext(Dispatchers.Main) {
                videoView?.get()?.let {
                    rtcEngine?.setupLocalVideo(VideoCanvas(null))
                }
            }

            // Destroy beauty SDK on IO dispatcher
            withContext(Dispatchers.IO) {
                ShengwangBeautySDK.unInitBeautySDK()
                agoraInitSuccess = false
            }
        }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun isMaterialCopied(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_MATERIAL_COPIED, false)
    }

    private fun setMaterialCopied(context: Context, copied: Boolean) {
        getSharedPreferences(context).edit().putBoolean(KEY_MATERIAL_COPIED, copied).apply()
    }

    private fun copyAssets(context: Context, assetsPath: String, targetPath: String) {
        // Get all files and folders under the assets directory assetDir
        val fileNames = context.resources.assets.list(assetsPath)
        // If it's a folder (directory), continue recursive traversal
        if (fileNames?.isNotEmpty() == true) {
            val targetFile = File(targetPath)
            if (!targetFile.exists() && !targetFile.mkdirs()) {
                return
            }
            for (fileName in fileNames) {
                copyAssets(
                    context,
                    "$assetsPath/$fileName",
                    "$targetPath/$fileName"
                )
            }
        } else {
            copyAssetsFile(context, assetsPath, targetPath)
        }
    }

    /**
     * Copy a single asset file to target path
     * @param context Android context
     * @param assetsFile Path to asset file
     * @param targetPath Target file path
     */
    private fun copyAssetsFile(context: Context, assetsFile: String, targetPath: String) {
        val dest = File(targetPath)
        dest.parentFile?.mkdirs()
        var input: InputStream? = null
        var output: OutputStream? = null
        try {
            input = BufferedInputStream(context.assets.open(assetsFile))
            output = BufferedOutputStream(FileOutputStream(dest))
            val buffer = ByteArray(1024)
            var length = 0
            while (input.read(buffer).also { length = it } != -1) {
                output.write(buffer, 0, length)
            }
        } catch (e: Exception) {
            Log.e(TAG, "copyAssetsFile", e)
        } finally {
            output?.close()
            input?.close()
        }
    }
}