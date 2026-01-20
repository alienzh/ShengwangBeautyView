package cn.shengwang.beauty.demo

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.TextureView
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import cn.shengwang.beauty.demo.databinding.ActivityBeautyExampleBinding
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.RtcEngineEx

/**
 * 美颜功能使用示例 Activity
 * 展示如何初始化和使用 BeautyManager
 * 使用本地美颜资源，不考虑远端下载
 */
class BeautyExampleActivity : AppCompatActivity() {

    private val TAG = "BeautyExampleActivity"

    private var _binding: ActivityBeautyExampleBinding? = null
    private val binding: ActivityBeautyExampleBinding get() = _binding!!

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (supportOnBackPressed()) {
                finish()
            }
        }
    }

    private var isInitialized = false
    private var videoView: View? = null

    private var rtcEngine: RtcEngineEx? = null
    var isFrontCamera = true

    private var channelName: String? = null
    private var uid: Int = 0 // RTC 用户 ID

    companion object {
        const val EXTRA_CHANNEL_NAME = "channel_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 获取频道号
        channelName = intent.getStringExtra(EXTRA_CHANNEL_NAME)
        if (channelName.isNullOrEmpty()) {
            Log.w(TAG, "Channel name is empty, finishing activity")
            finish()
            return
        }
        Log.d(TAG, "Channel name: $channelName")

        _binding = ActivityBeautyExampleBinding.inflate(layoutInflater)
        if (_binding?.root == null) {
            finish()
            return
        }
        setContentView(_binding!!.root)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        setupSystemBarsAndCutout(ImmersiveMode.SEMI_IMMERSIVE, false)
        initView()
    }

    private fun supportOnBackPressed(): Boolean = true

    private fun initView() {
        // 保持屏幕常亮
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // 设置返回按钮点击事件
        binding.ivBack.setOnClickListener {
            finish()
        }

        // 设置美颜按钮点击事件 - 直接显示/隐藏 View
        binding.tvBeauty.setOnClickListener {
            val beautyView = binding.beautyControlView
            beautyView.visibility = if (beautyView.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        // 设置切换摄像头按钮点击事件
        binding.tvSwitchCamera.setOnClickListener {
            if (isInitialized) {
                isFrontCamera = !isFrontCamera
                rtcEngine?.switchCamera()
            }
        }

        // 权限已在 MainActivity 中申请，直接初始化美颜功能
        initializeBeauty()

        joinChannel()
    }

    private fun initializeBeauty() {
        if (isInitialized) return
        isInitialized = true

        rtcEngine = createRtcEngine()

        videoView = TextureView(this).apply {
            binding.flVideoContainer.addView(this)
        }

        // 注意：BeautyManager.initialize 会注册 VideoFrameObserver
        BeautyManager.initialize(this, rtcEngine!!)

        BeautyManager.setupLocalVideo(videoView!!, Constants.RENDER_MODE_HIDDEN)
    }

    private fun createRtcEngine(): RtcEngineEx {
        val config = RtcEngineConfig()
        config.mContext = App.instance()
        config.mAppId = BuildConfig.Shengwang_App_ID
        config.addExtension("agora_ai_echo_cancellation_extension")
        config.addExtension("agora_ai_noise_suppression_extension")
        config.mEventHandler = object : IRtcEngineEventHandler() {
            override fun onError(err: Int) {
                super.onError(err)
                Log.d(TAG, "Rtc Error code err: $err, msg:" + RtcEngine.getErrorDescription(err))
            }

            override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
                super.onJoinChannelSuccess(channel, uid, elapsed)
                Log.d(TAG, "onJoinChannelSuccess channel: $channel, uid: $uid")
            }

            override fun onLeaveChannel(stats: RtcStats?) {
                super.onLeaveChannel(stats)
                Log.d(TAG, "onLeaveChannel")
            }
        }
        return (RtcEngine.create(config) as RtcEngineEx).apply {
            enableVideo()
        }
    }

    override fun onResume() {
        super.onResume()
        // 如果已初始化，则开始预览并加入频道
        if (isInitialized && rtcEngine != null) {
            rtcEngine?.startPreview()
        }
    }

    /**
     * 加入 RTC 频道
     */
    private fun joinChannel() {
        val name = channelName ?: return
        val token: String? = null // 如果使用 token，需要从服务器获取
        val result = rtcEngine?.joinChannel(token, name, uid, null) ?: -1
        if (result == Constants.ERR_OK) {
            Log.d("BeautyExampleActivity", "Join channel success: $name, uid: $uid")
        } else {
            Log.e("BeautyExampleActivity", "Join channel failed: ${RtcEngine.getErrorDescription(result)}")
        }
    }

    override fun onPause() {
        super.onPause()
        if (isInitialized) {
            rtcEngine?.stopPreview()
        }
    }

    override fun finish() {
        onBackPressedCallback.remove()
        super.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isInitialized && rtcEngine != null) {
            rtcEngine?.stopPreview()
            rtcEngine?.leaveChannel()

            videoView?.let {
                binding.flVideoContainer.removeView(it)
            }

            BeautyManager.destroy()
        }
        _binding = null
    }

    /**
     * Sets up immersive display and notch screen adaptation
     * @param immersiveMode Type of immersive mode
     * @param lightStatusBar Whether to use dark status bar icons
     */
    private fun setupSystemBarsAndCutout(
        immersiveMode: ImmersiveMode = ImmersiveMode.EDGE_TO_EDGE,
        lightStatusBar: Boolean = false
    ) {
        // Step 1: Set up basic Edge-to-Edge display
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+
            window.setDecorFitsSystemWindows(false)
            WindowCompat.getInsetsController(window, window.decorView).apply {
                isAppearanceLightStatusBars = lightStatusBar
            }
        } else {
            @Suppress("DEPRECATION")
            var flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)

            if (lightStatusBar) {
                flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }

            window.decorView.systemUiVisibility = flags
        }

        // Step 2: Set system bar transparency
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        // Step 3: Handle notch screens
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        // Step 4: Set system UI visibility based on immersive mode
        when (immersiveMode) {
            ImmersiveMode.EDGE_TO_EDGE -> {
                // Do not hide any system bars, only extend content to full screen
                // Already set in step 1
            }

            ImmersiveMode.SEMI_IMMERSIVE -> {
                // Hide navigation bar, show status bar
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    window.insetsController?.apply {
                        hide(WindowInsets.Type.navigationBars())
                        show(WindowInsets.Type.statusBars())
                        systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    }
                } else {
                    @Suppress("DEPRECATION")
                    window.decorView.systemUiVisibility = (window.decorView.systemUiVisibility
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
                }
            }

            ImmersiveMode.FULLY_IMMERSIVE -> {
                // Hide all system bars
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    window.insetsController?.apply {
                        hide(WindowInsets.Type.systemBars())
                        systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    }
                } else {
                    @Suppress("DEPRECATION")
                    window.decorView.systemUiVisibility = (window.decorView.systemUiVisibility
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
                }
            }
        }
    }

    /**
     * Immersive mode types
     */
    enum class ImmersiveMode {
        /**
         * Content extends under system bars, but system bars remain visible
         */
        EDGE_TO_EDGE,

        /**
         * Hide navigation bar, show status bar
         */
        SEMI_IMMERSIVE,

        /**
         * Hide all system bars, fully immersive
         */
        FULLY_IMMERSIVE
    }
}