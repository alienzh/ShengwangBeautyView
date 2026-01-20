package cn.shengwang.beauty.demo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.shengwang.beauty.demo.databinding.ActivityMainBinding
import cn.shengwang.beauty.demo.utils.PermissionHelp
import java.util.Random

/**
 * 主界面 Activity
 * 提供频道名称输入和加入频道功能
 */
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = _binding!!

    private lateinit var permissionHelp: PermissionHelp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        if (_binding?.root == null) {
            finish()
            return
        }
        setContentView(_binding!!.root)

        permissionHelp = PermissionHelp(this)

        // 生成默认随机频道名称
        val defaultChannelName = generateRandomChannelName()
        binding.etChannelName.setText(defaultChannelName)
        binding.etChannelName.setSelection(defaultChannelName.length)

        // 设置加入频道按钮点击事件
        binding.btnJoinChannel.setOnClickListener {
            val channelName = binding.etChannelName.text.toString().trim()
            if (channelName.isEmpty()) {
                Toast.makeText(
                    this,
                    "请输入频道名称",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // 申请权限
            permissionHelp.checkCameraAndMicPerms(
                granted = {
                    // 权限已授予，跳转到 BeautyExampleActivity
                    navigateToBeautyExample(channelName)
                },
                unGranted = {
                    // 权限被拒绝
                    Toast.makeText(
                        this,
                        "需要摄像头和麦克风权限才能使用美颜功能",
                        Toast.LENGTH_LONG
                    ).show()
                },
                force = false
            )
        }
    }

    /**
     * 生成随机频道名称
     * 格式：shengwang_beauty_xxxxx（10000～99999）
     */
    private fun generateRandomChannelName(): String {
        val random = Random()
        val randomNumber = random.nextInt(90000) + 10000 // 10000 到 99999
        return "shengwang_beauty_$randomNumber"
    }

    /**
     * 跳转到 BeautyExampleActivity
     */
    private fun navigateToBeautyExample(channelName: String) {
        val intent = Intent(this, BeautyExampleActivity::class.java).apply {
            putExtra(EXTRA_CHANNEL_NAME, channelName)
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val EXTRA_CHANNEL_NAME = "channel_name"
    }
}
