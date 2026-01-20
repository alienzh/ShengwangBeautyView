package cn.shengwang.beauty.ui.builder

import cn.shengwang.beauty.R
import cn.shengwang.beauty.core.ShengwangBeautySDK
import cn.shengwang.beauty.core.FilterNames
import cn.shengwang.beauty.ui.contract.IPageBuilder
import cn.shengwang.beauty.ui.model.BeautyPageInfo
import cn.shengwang.beauty.ui.model.BeautyItemInfo
import cn.shengwang.beauty.ui.model.BeautyItemType
import cn.shengwang.beauty.ui.model.BeautyModule

/**
 * 滤镜模块页面构建器
 * 负责构建滤镜模块的页面信息
 * 
 * 注意：此构建器为内部实现，不对外暴露
 */
internal class FilterPageBuilder(
    private val beautyConfig: ShengwangBeautySDK.BeautyConfig
) : IPageBuilder {

    override fun buildPage(): BeautyPageInfo {
        val filterItems = mutableListOf<BeautyItemInfo>()

        filterItems.add(
            BeautyItemInfo(
                R.string.beauty_effect_none,
                R.drawable.beauty_ic_none,
                isSelected = beautyConfig.filterName == null,
                showSlider = false,
                type = BeautyItemType.NONE,
                onItemClick = {
                    beautyConfig.filterName = null
                }
            )
        )

        // 滤镜选项
        // 暖色系
        // 沉稳
        addFilterItem(
            filterItems,
            R.string.beauty_filter_serene,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.SERENE
        )
        // 都市
        addFilterItem(
            filterItems,
            R.string.beauty_filter_urban,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.URBAN
        )
        // 流光
        addFilterItem(
            filterItems,
            R.string.beauty_filter_glow,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.GLOW
        )
        // 流金
        addFilterItem(
            filterItems,
            R.string.beauty_filter_gilt,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.GILT
        )
        // 奶油
        addFilterItem(
            filterItems,
            R.string.beauty_filter_cream,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.CREAM
        )
        // 拿铁
        addFilterItem(
            filterItems,
            R.string.beauty_filter_latte,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.LATTE
        )
        // 柠夏
        addFilterItem(
            filterItems,
            R.string.beauty_filter_summer,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.SUMMER
        )
        // 日常
        addFilterItem(
            filterItems,
            R.string.beauty_filter_daily,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.DAILY
        )
        // 绅士
        addFilterItem(
            filterItems,
            R.string.beauty_filter_gentleman,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.GENTLEMAN
        )
        // 香草
        addFilterItem(
            filterItems,
            R.string.beauty_filter_vanilla,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.VANILLA
        )

        // 冷/白色系
        // 白瓷
        addFilterItem(
            filterItems,
            R.string.beauty_filter_bright,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.BRIGHT
        )
        // 白桃
        addFilterItem(
            filterItems,
            R.string.beauty_filter_peach,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.PEACH
        )
        // 苍墨
        addFilterItem(
            filterItems,
            R.string.beauty_filter_ink,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.INK
        )
        // 胶片
        addFilterItem(
            filterItems,
            R.string.beauty_filter_film,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.FILM
        )
        // 霁晴
        addFilterItem(
            filterItems,
            R.string.beauty_filter_sunny,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.SUNNY
        )
        // 漫画
        addFilterItem(
            filterItems,
            R.string.beauty_filter_comic,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.COMIC
        )
        // 梦幻
        addFilterItem(
            filterItems,
            R.string.beauty_filter_dreamy,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.DREAMY
        )
        // 棉绒
        addFilterItem(
            filterItems,
            R.string.beauty_filter_cotton,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.COTTON
        )
        // 苏打
        addFilterItem(
            filterItems,
            R.string.beauty_filter_soda,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.SODA
        )
        // 月白
        addFilterItem(
            filterItems,
            R.string.beauty_filter_moonlight,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.MOONLIGHT
        )

        // 氛围系
        // 白茶
        addFilterItem(
            filterItems,
            R.string.beauty_filter_white_tea,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.WHITE_TEA
        )
        // 沉谧
        addFilterItem(
            filterItems,
            R.string.beauty_filter_tranquil,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.TRANQUIL
        )
        // ins风
        addFilterItem(
            filterItems, 
            R.string.beauty_filter_insta_style, 
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.INSTA_STYLE
        )
        // 老街
        addFilterItem(
            filterItems,
            R.string.beauty_filter_street,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.STREET
        )
        // 泡芙
        addFilterItem(
            filterItems,
            R.string.beauty_filter_puff,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.PUFF
        )
         // 私藏
         addFilterItem(
            filterItems,
            R.string.beauty_filter_collection,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.COLLECTION
        )
        // 盐汽水
        addFilterItem(
            filterItems,
            R.string.beauty_filter_salty,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.SALTY
        )
        // 质感
        addFilterItem(
            filterItems,
            R.string.beauty_filter_texture,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.TEXTURE
        )
        // 气色
        addFilterItem(
            filterItems,
            R.string.beauty_filter_colorful,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.COLORFUL
        )

        // 环境系
        // 初雪
        addFilterItem(
            filterItems,
            R.string.beauty_filter_snow,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.SNOW
        )
        // 粉霞
        addFilterItem(
            filterItems,
            R.string.beauty_filter_blush,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.BLUSH
        )
        // 怀旧
        addFilterItem(
            filterItems,
            R.string.beauty_filter_nostalgia,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.NOSTALGIA
        )
        // 焦糖
        addFilterItem(
            filterItems,
            R.string.beauty_filter_caramel,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.CARAMEL
        )
        // 微醺
        addFilterItem(
            filterItems,
            R.string.beauty_filter_tipsy,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.TIPSY
        )
        // 薰衣草
        addFilterItem(
            filterItems,
            R.string.beauty_filter_lavender,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.LAVENDER
        )
        // 胭脂
        addFilterItem(
            filterItems,
            R.string.beauty_filter_rouge,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.ROUGE
        )
        // 氤氲
        addFilterItem(
            filterItems,
            R.string.beauty_filter_misty,
            R.drawable.beauty_ic_filter_lengbai,
            FilterNames.MISTY
        )
       

        return BeautyPageInfo(
            R.string.beauty_group_filter,
            filterItems,
            type = BeautyModule.FILTER
        )
    }

    private fun addFilterItem(
        items: MutableList<BeautyItemInfo>,
        nameRes: Int,
        iconRes: Int,
        filterName: String
    ) {
        items.add(
            BeautyItemInfo(
                nameRes,
                iconRes,
                beautyConfig.filterStrength,
                isSelected = beautyConfig.filterName == filterName,
                valueRange = 0f..1.0f,
                onValueChanged = { value ->
                    beautyConfig.filterStrength = value
                },
                onItemClick = { itemInfo ->
                    beautyConfig.filterName = filterName
                    itemInfo.value = beautyConfig.filterStrength
                }
            )
        )
    }
}
