package cn.shengwang.beauty.ui.builder

import cn.shengwang.beauty.R
import cn.shengwang.beauty.core.ShengwangBeautySDK
import cn.shengwang.beauty.core.MakeupNames
import cn.shengwang.beauty.ui.contract.IPageBuilder
import cn.shengwang.beauty.ui.model.BeautyPageInfo
import cn.shengwang.beauty.ui.model.BeautyItemInfo
import cn.shengwang.beauty.ui.model.BeautyItemType
import cn.shengwang.beauty.ui.model.BeautyModule

/**
 * 风格妆模块页面构建器
 * 负责构建风格妆模块的页面信息
 * 
 * 注意：此构建器为内部实现，不对外暴露
 */
internal class MakeupPageBuilder(
    private val beautyConfig: ShengwangBeautySDK.BeautyConfig
) : IPageBuilder {

    override fun buildPage(): BeautyPageInfo {
        val makeupItems = mutableListOf<BeautyItemInfo>()
        
        makeupItems.add(
            BeautyItemInfo(
                R.string.beauty_effect_none,
                R.drawable.beauty_ic_none,
                isSelected = beautyConfig.makeupName == null,
                showSlider = false,
                type = BeautyItemType.NONE,
                onItemClick = {
                    beautyConfig.makeupName = null
                }
            )
        )

        // 美妆选项
        // 学妹妆
        addMakeupItem(
            makeupItems,
            R.string.beauty_makeup_young,
            R.drawable.beauty_ic_effect_tianmei,
            MakeupNames.YOUNG
        )
        // 学姐妆
        addMakeupItem(
            makeupItems,
            R.string.beauty_makeup_mature,
            R.drawable.beauty_ic_effect_tianmei,
            MakeupNames.MATURE
        )
        // 气质妆
        addMakeupItem(
            makeupItems,
            R.string.beauty_makeup_aura,
            R.drawable.beauty_ic_effect_tianmei,
            MakeupNames.AURA
        )
        // 白皙妆
        addMakeupItem(
            makeupItems,
            R.string.beauty_makeup_natural,
            R.drawable.beauty_ic_effect_tianmei,
            MakeupNames.NATURAL
        )
        // 优雅妆
        addMakeupItem(
            makeupItems,
            R.string.beauty_makeup_graceful,
            R.drawable.beauty_ic_effect_tianmei,
            MakeupNames.GRACEFUL
        )
        // 粉晕妆
        addMakeupItem(
            makeupItems,
            R.string.beauty_makeup_charm,
            R.drawable.beauty_ic_effect_tianmei,
            MakeupNames.CHARM
        )
        // 俏皮妆
        addMakeupItem(
            makeupItems,
            R.string.beauty_makeup_perky,
            R.drawable.beauty_ic_effect_tianmei,
            MakeupNames.PERKY
        )
        // 少女妆
        addMakeupItem(
            makeupItems,
            R.string.beauty_makeup_maiden,
            R.drawable.beauty_ic_effect_tianmei,
            MakeupNames.MAIDEN
        )
        // 深邃妆
        addMakeupItem(
            makeupItems,
            R.string.beauty_makeup_insight,
            R.drawable.beauty_ic_effect_tianmei,
            MakeupNames.INSIGHT
        )
        // 氤氲妆
        addMakeupItem(
            makeupItems,
            R.string.beauty_makeup_misty,
            R.drawable.beauty_ic_effect_tianmei,
            MakeupNames.MISTY
        )

        return BeautyPageInfo(
            R.string.beauty_group_makeup,
            makeupItems,
            type = BeautyModule.STYLE_MAKEUP
        )
    }

    private fun addMakeupItem(
        items: MutableList<BeautyItemInfo>,
        nameRes: Int,
        iconRes: Int,
        makeupName: String
    ) {
        items.add(
            BeautyItemInfo(
                nameRes,
                iconRes,
                beautyConfig.makeupIntensity,
                isSelected = beautyConfig.makeupName == makeupName,
                valueRange = 0f..1.0f,
                onValueChanged = { value ->
                    beautyConfig.makeupIntensity = value
                },
                onItemClick = { itemInfo ->
                    beautyConfig.makeupName = makeupName
                    itemInfo.value = beautyConfig.makeupIntensity
                }
            )
        )
    }
}
