package cn.shengwang.beauty.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import cn.shengwang.beauty.R
import cn.shengwang.beauty.core.ShengwangBeautySDK
import cn.shengwang.beauty.databinding.ShengwangBeautyViewBinding
import cn.shengwang.beauty.databinding.ShengwangBeautyControlPageBinding
import cn.shengwang.beauty.databinding.ShengwangBeautyControlItemBinding
import cn.shengwang.beauty.ui.builder.BeautyPageBuilder
import cn.shengwang.beauty.ui.model.BeautyPageInfo
import cn.shengwang.beauty.ui.model.BeautyItemInfo
import cn.shengwang.beauty.ui.builder.MakeupPageBuilder
import cn.shengwang.beauty.ui.builder.FilterPageBuilder
import cn.shengwang.beauty.ui.builder.StickerPageBuilder
import cn.shengwang.beauty.ui.model.BeautyItemType
import cn.shengwang.beauty.ui.model.BeautyModule

/**
 * 声网美颜控制视图
 * 专门用于声网美颜的控制界面
 *
 * 支持：
 * - 模块级开关：快速删除整个一级菜单模块
 * - 原子能力级精简：隐藏模块内的特定参数
 *
 * 设计原则：
 * - UI 组件与 SDK API 建立 1:1 映射，避免中间层二次封装
 * - 值转换在 UI 层完成，传递给 SDK 的值是 SDK 期望的原始值范围
 * - 确保 UI 表现与算法效果完全同步
 */
class ShengwangBeautyView : android.widget.FrameLayout {

    private val viewBinding by lazy {
        ShengwangBeautyViewBinding.inflate(LayoutInflater.from(context))
    }

    // 使用 ShengwangBeautySDK.beautyConfig 直接访问配置
    private val beautyConfig: ShengwangBeautySDK.BeautyConfig
        get() = ShengwangBeautySDK.beautyConfig

    private val itemAdapterList = mutableListOf<ItemAdapter?>()

    private val pageAdapter by lazy {
        object : RecyclerView.Adapter<PageViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
                val binding = ShengwangBeautyControlPageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                binding.root.layoutParams = ViewGroup.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT
                )
                return PageViewHolder(binding)
            }

            override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
                val pageInfo = pageList.getOrNull(position) ?: return
                val itemAdapter = createItemAdapter(position)
                itemAdapter.updateItems(pageInfo.itemList)
                // Ensure the list is large enough before adding at the specified position
                while (itemAdapterList.size <= position) {
                    itemAdapterList.add(null)
                }
                itemAdapterList[position] = itemAdapter

                (context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager)?.defaultDisplay?.width?.let {
                    holder.binding.recycleView.layoutParams =
                        holder.binding.recycleView.layoutParams.apply {
                            width = it
                        }
                }
                holder.binding.recycleView.adapter = itemAdapter
            }

            override fun getItemCount() = pageList.size
        }
    }

    var pageList = listOf<BeautyPageInfo>()
        set(value) {
            field = value
            pageAdapter.notifyDataSetChanged()
            itemAdapterList.clear()
            viewBinding.tabLayout.removeAllTabs()
            value.forEach {
                val tab = viewBinding.tabLayout.newTab().setText(it.name)
                viewBinding.tabLayout.addTab(tab)
                if (it.isSelected) {
                    tab.select()
                }
            }
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: android.util.AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: android.util.AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        ShengwangBeautySDK.beautyStateListener = {
            post {
                refreshPageList()
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        ShengwangBeautySDK.beautyStateListener = null
    }

    private fun initView(context: Context) {
        addView(viewBinding.root)
        viewBinding.viewPager.isUserInputEnabled = false
        pageList = onPageListCreate()
        viewBinding.viewPager.adapter = pageAdapter

        viewBinding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val selectedPosition = viewBinding.tabLayout.selectedTabPosition
                pageList.forEachIndexed { index, pageInfo ->
                    pageInfo.isSelected = index == selectedPosition
                }
                // 安全获取当前页面的选中项索引
                val currentPage = pageList.getOrNull(selectedPosition)
                var itemIndex = currentPage?.itemList?.indexOfFirst { it.isSelected } ?: -1
                if (itemIndex < 0) {
                    itemIndex = 0
                }
//                onSelectedChanged(selectedPosition, itemIndex)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        TabLayoutMediator(viewBinding.tabLayout, viewBinding.viewPager) { tab, position ->
            tab.text = context.getString(pageList[position].name)
        }.attach()
    }

    private fun createItemAdapter(pageIndex: Int) =
        ItemAdapter(pageIndex) { position ->
            // 安全获取当前页面和选中项
            val currentPage = pageList.getOrNull(pageIndex) ?: return@ItemAdapter
            val itemInfo = currentPage.itemList.getOrNull(position) ?: return@ItemAdapter
            val previouslySelectedItem = currentPage.itemList.firstOrNull { it.isSelected }
            previouslySelectedItem?.isSelected = false
            itemInfo.isSelected = true
            onSelectedChanged(pageIndex, position)
        }

    protected fun onPageListCreate(): List<BeautyPageInfo> {
        // 构建页面列表，使用独立的构建器类
        val pageList = mutableListOf<BeautyPageInfo>()

        // 1. BEAUTY 模块（美颜：美肤+美型+画质）
        val beautyBuilder = BeautyPageBuilder(
            beautyConfig = beautyConfig,
            refreshPageList = {
                refreshPageList()
            }
        )
        pageList.add(beautyBuilder.buildPage())

        // 2. STYLE_MAKEUP 模块（风格妆）
        val makeupBuilder = MakeupPageBuilder(beautyConfig)
        pageList.add(makeupBuilder.buildPage())

        // 3. FILTER 模块（滤镜）
        val filterBuilder = FilterPageBuilder(beautyConfig)
        pageList.add(filterBuilder.buildPage())

        // 4. STICKER 模块（贴纸）
        val stickerBuilder = StickerPageBuilder(beautyConfig)
        pageList.add(stickerBuilder.buildPage())

        return pageList
    }

    /**
     * 刷新页面列表
     * 重新调用 onPageListCreate() 生成页面列表，并更新UI显示
     *
     * 注意：此方法会触发页面列表的重新构建，可能导致当前选中状态丢失
     * 建议在配置变化时调用，而不是频繁调用
     */
    fun refreshPageList() {
        pageList = onPageListCreate()
    }

    /**
     * 选中项变化时的回调
     *
     * @param pageIndex 页面索引
     * @param itemIndex 功能项索引
     */
    protected fun onSelectedChanged(pageIndex: Int, itemIndex: Int) {
        // 安全获取页面和功能项信息
        val pageInfo = pageList.getOrNull(pageIndex) ?: return
        val itemInfo = pageInfo.itemList.getOrNull(itemIndex) ?: return

        // 先清除所有监听器
        viewBinding.slider.clearOnChangeListeners()
        viewBinding.slider.clearOnSliderTouchListeners()

        // 根据是否需要显示滑动条来设置UI
        if (itemInfo.showSlider) {
            setupSlider(itemInfo)
            viewBinding.slider.visibility = VISIBLE
        } else {
            viewBinding.slider.visibility = INVISIBLE
        }

        // Handle special items by name (for backward compatibility)
        // Note: It's recommended to use onItemClick callback in ItemInfo instead
//        if (itemInfo.name == R.string.beauty_item_reset) {
//            // 重置所有参数为默认值
//            resetBeauty()
//            // 刷新 UI 显示
//            refreshPageList()
//        }
    }

    /**
     * 设置滑动条的参数和监听器
     *
     * @param itemInfo 功能项信息，包含值范围、当前值等
     *
     * 注意：
     * - 会先清除所有监听器，避免设置值时触发回调
     * - 值变化监听器只在滑动过程中更新UI，不触发业务回调
     * - 业务回调只在用户释放滑动条时触发（onStopTrackingTouch）
     */
    private fun setupSlider(itemInfo: BeautyItemInfo) {
        // 先清除所有监听器，避免设置值时触发回调
        viewBinding.slider.clearOnChangeListeners()
        viewBinding.slider.clearOnSliderTouchListeners()

        // 设置范围
        viewBinding.slider.valueFrom = itemInfo.valueRange.start
        viewBinding.slider.valueTo = itemInfo.valueRange.endInclusive

        // 设置值（此时没有监听器，不会触发回调）
        // 注意：由于在 onSelectedChanged 中已经先调用了回调，所以这里设置值不会触发循环
        if (itemInfo.valueRange.endInclusive > 1) {
            viewBinding.slider.value = itemInfo.value.toInt().toFloat()
        } else {
            viewBinding.slider.value = itemInfo.value
        }

        // 设置格式化器
        viewBinding.slider.setLabelFormatter { value ->
            if (itemInfo.valueRange.endInclusive > 1) {
                value.toInt().toString()
            } else {
                String.format("%.1f", value)
            }
        }

        // 设置值后再添加监听器，避免初始化时触发回调
        viewBinding.slider.addOnChangeListener { _, value, _ ->
            // Only update itemInfo.value during sliding, don't call callback
            if (itemInfo.valueRange.endInclusive > 1) {
                itemInfo.value = value.toInt().toFloat()
            } else {
                itemInfo.value = value
            }
        }

        viewBinding.slider.addOnSliderTouchListener(object :
            com.google.android.material.slider.Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: com.google.android.material.slider.Slider) {
                // Do nothing when start tracking
            }

            override fun onStopTrackingTouch(slider: com.google.android.material.slider.Slider) {
                // Call callback only when user releases the slider
                val value = slider.value
                if (itemInfo.valueRange.endInclusive > 1) {
                    val intValue = value.toInt()
                    itemInfo.value = intValue.toFloat()
                    itemInfo.onValueChanged?.invoke(intValue.toFloat())
                } else {
                    itemInfo.value = value
                    itemInfo.onValueChanged?.invoke(value)
                }
            }
        })
    }

    /**
     * 更新功能项信息
     *
     * @param updater 更新函数，返回true表示找到并更新了目标项
     *
     * 注意：此方法会遍历所有页面和功能项，找到第一个匹配的项并更新
     * 更新后会触发该功能项的值变化回调
     */
    fun updateItemInfo(updater: (itemInfo: BeautyItemInfo) -> Boolean) {
        pageList.forEach { pageInfo ->
            pageInfo.itemList.forEach { itemInfo ->
                if (updater(itemInfo)) {
                    itemInfo.onValueChanged?.invoke(itemInfo.value)
                    return
                }
            }
        }
    }

    /**
     * 重置操作：
     * 重置恢复为出厂时模板内的参数值。
     *
     * 注意；保存与重置操作后，下次addOrUpdate加载节点时会自动生效
     *
     * @param type 页面类型（对应 SDK VIDEO_EFFECT_NODE_ID）
     */
    fun resetBeauty(type: BeautyModule = BeautyModule.BEAUTY) {
        beautyConfig.resetBeauty(type)
        // 刷新整个页面列表以更新开关状态和其他参数值
        refreshPageList()
    }

    /**
     * 保存操作：
     * 将主播调完参后的参数直播保存到本地，下次addOrUpdate加载节点时会自动调用之前保存好的参数。
     *
     * 注意；保存与重置操作后，下次addOrUpdate加载节点时会自动生效
     *
     * @param type 页面类型（对应 SDK VIDEO_EFFECT_NODE_ID）
     */
    fun saveBeauty(type: BeautyModule = BeautyModule.BEAUTY) {
        beautyConfig.saveBeauty(type)
    }

    // ViewHolder classes
    private class PageViewHolder(val binding: ShengwangBeautyControlPageBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class ItemViewHolder(val binding: ShengwangBeautyControlItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    // ItemAdapter
    private inner class ItemAdapter(
        private val pageIndex: Int,
        private val onItemClick: (Int) -> Unit
    ) : RecyclerView.Adapter<ItemViewHolder>() {

        private var items = listOf<BeautyItemInfo>()
        private var selectedHolder: ItemViewHolder? = null

        fun updateItems(newItems: List<BeautyItemInfo>) {
            items = newItems
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val binding = ShengwangBeautyControlItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ItemViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val itemInfo = items.getOrNull(position) ?: return
            holder.binding.ivIcon.setImageResource(itemInfo.icon)
            holder.binding.ivIcon.isActivated = itemInfo.isSelected
            holder.binding.tvName.setText(itemInfo.name)

            if (itemInfo.type == BeautyItemType.TOGGLE) {
                holder.binding.ivIcon.background = null
            } else {
                holder.binding.ivIcon.setBackgroundResource(R.drawable.beauty_item_bg)
            }

            if (itemInfo.isSelected) {
                selectedHolder = holder
                val currentPage = pageList.getOrNull(pageIndex)
                if (currentPage?.isSelected == true) {
                    // Initialize UI (onValueChanged will be called in onSelectedChanged before setting slider)
                    onSelectedChanged(pageIndex, position)
                }
            }

            holder.binding.ivIcon.setOnClickListener {
                // 调用 item 特定的 onClick 回调（更新配置和 itemInfo 属性）
                itemInfo.onItemClick?.invoke(itemInfo)

                // 重置项：刷新整个列表
                if (itemInfo.type == BeautyItemType.RESET) {
                    // 逻辑已经在 onItemClick 中处理（包括 refreshPageList），这里只需要确保 UI 响应
                    // 由于 onItemClick 中调用了 refreshPageList，这里其实不需要做太多
                    return@setOnClickListener
                }

                // 普通参数项：更新选中状态并触发选中变化回调
                val currentPage = pageList.getOrNull(pageIndex)
                val previouslySelectedItem = currentPage?.itemList?.firstOrNull { it.isSelected }
                previouslySelectedItem?.isSelected = false
                itemInfo.isSelected = true
                selectedHolder?.binding?.ivIcon?.isActivated = false
                holder.binding.ivIcon.isActivated = true
                selectedHolder = holder
                onItemClick.invoke(position)
            }
        }

        override fun getItemCount() = items.size
    }

}
