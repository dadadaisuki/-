package com.travel.superapp.ui.screens.guidelist

import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/** 列表查找方式：按地区 / 按距离 / 按好评 */
enum class GuideFindMode {
    ByRegion,
    ByDistance,
    ByGoodRating,
}

/** 距离档位（米），「按距离」「按好评」共用 */
enum class DistanceTier(val label: String, val meters: Int) {
    M100("100m内", 100),
    M200("200m内", 200),
    M500("500m内", 500),
    KM1("1km内", 1_000),
    KM2("2km内", 2_000),
}

/** 按地区、按距离时的排序：默认（综合） / 评分 / 价格 */
enum class GuideSortGeneral {
    DefaultComposite,
    ByRating,
    ByPrice,
}

/** 按好评时使用固定排序：星级优先，次注重价格便宜 */

data class GeoPoint(
    val latitude: Double,
    val longitude: Double,
)

data class Guide(
    val id: String,
    val name: String,
    /** 展示用城市文案 */
    val cityDisplay: String,
    val province: String,
    val city: String,
    val district: String,
    val latitude: Double,
    val longitude: Double,
    /** 1.0 - 5.0，保留一位小数展示 */
    val rating: Double,
    /** 用于排序与展示 */
    val priceYuanPerDay: Int,
    val priceLabel: String,
)

/** 模拟用户位置（后续可替换为 GPS） */
val MockUserLocationHangzhou = GeoPoint(30.2741, 120.1551)

fun haversineMeters(a: GeoPoint, b: GeoPoint): Double {
    val r = 6_371_000.0
    val p1 = Math.toRadians(a.latitude)
    val p2 = Math.toRadians(b.latitude)
    val dLat = Math.toRadians(b.latitude - a.latitude)
    val dLon = Math.toRadians(b.longitude - a.longitude)
    val h = sin(dLat / 2) * sin(dLat / 2) +
        cos(p1) * cos(p2) * sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * asin(sqrt(h.coerceIn(0.0, 1.0)))
    return r * c
}

fun Guide.toGeoPoint(): GeoPoint = GeoPoint(latitude, longitude)

fun distanceToUserMeters(guide: Guide, user: GeoPoint): Double =
    haversineMeters(user, guide.toGeoPoint())

/** 综合排序：兼顾评分与价格（评分越高越好，价越低越好） */
fun compositeSortKey(guide: Guide): Double =
    guide.rating * 1_000.0 - guide.priceYuanPerDay / 20.0

fun Guide.matchesRegion(
    province: String?,
    city: String?,
    district: String?,
): Boolean {
    if (province != null && province.isNotBlank() && province != this.province) return false
    if (city != null && city.isNotBlank() && city != this.city) return false
    if (district != null && district.isNotBlank() && district != this.district) return false
    return true
}

fun List<Guide>.filteredAndSorted(
    mode: GuideFindMode,
    user: GeoPoint,
    regionProvince: String?,
    regionCity: String?,
    regionDistrict: String?,
    distanceTier: DistanceTier,
    sortGeneral: GuideSortGeneral,
): List<Guide> {
    val base = when (mode) {
        GuideFindMode.ByRegion -> filter {
            it.matchesRegion(regionProvince, regionCity, regionDistrict)
        }
        GuideFindMode.ByDistance -> filter {
            distanceToUserMeters(it, user) <= distanceTier.meters.toDouble()
        }
        GuideFindMode.ByGoodRating -> filter {
            distanceToUserMeters(it, user) <= distanceTier.meters.toDouble()
        }
    }

    return when (mode) {
        GuideFindMode.ByGoodRating -> base.sortedWith(
            compareByDescending<Guide> { it.rating }
                .thenBy { it.priceYuanPerDay }
                .thenBy { it.id },
        )
        else -> when (sortGeneral) {
            GuideSortGeneral.DefaultComposite -> base.sortedWith(
                compareByDescending<Guide> { compositeSortKey(it) }
                    .thenByDescending { it.rating }
                    .thenBy { it.priceYuanPerDay }
                    .thenBy { it.id },
            )
            GuideSortGeneral.ByRating -> base.sortedWith(
                compareByDescending<Guide> { it.rating }
                    .thenBy { it.priceYuanPerDay }
                    .thenBy { it.id },
            )
            GuideSortGeneral.ByPrice -> base.sortedWith(
                compareBy<Guide> { it.priceYuanPerDay }
                    .thenByDescending { it.rating }
                    .thenBy { it.id },
            )
        }
    }
}

fun demoGuides(): List<Guide> = listOf(
    Guide(
        id = "1",
        name = "林晓",
        cityDisplay = "杭州市 · 西湖区",
        province = "浙江省",
        city = "杭州市",
        district = "西湖区",
        latitude = 30.2591,
        longitude = 120.1303,
        rating = 4.9,
        priceYuanPerDay = 680,
        priceLabel = "¥680/天",
    ),
    Guide(
        id = "2",
        name = "王磊",
        cityDisplay = "杭州市 · 上城区",
        province = "浙江省",
        city = "杭州市",
        district = "上城区",
        latitude = 30.2446,
        longitude = 120.1804,
        rating = 4.7,
        priceYuanPerDay = 520,
        priceLabel = "¥520/天",
    ),
    Guide(
        id = "3",
        name = "陈悦",
        cityDisplay = "杭州市 · 滨江区",
        province = "浙江省",
        city = "杭州市",
        district = "滨江区",
        latitude = 30.1876,
        longitude = 120.2103,
        rating = 5.0,
        priceYuanPerDay = 880,
        priceLabel = "¥880/天",
    ),
    Guide(
        id = "4",
        name = "赵敏",
        cityDisplay = "宁波市 · 海曙区",
        province = "浙江省",
        city = "宁波市",
        district = "海曙区",
        latitude = 29.8730,
        longitude = 121.5500,
        rating = 4.3,
        priceYuanPerDay = 460,
        priceLabel = "¥460/天",
    ),
    Guide(
        id = "5",
        name = "周航",
        cityDisplay = "南京市 · 玄武区",
        province = "江苏省",
        city = "南京市",
        district = "玄武区",
        latitude = 32.0486,
        longitude = 118.7975,
        rating = 4.6,
        priceYuanPerDay = 590,
        priceLabel = "¥590/天",
    ),
    Guide(
        id = "6",
        name = "孙宁",
        cityDisplay = "苏州市 · 姑苏区",
        province = "江苏省",
        city = "苏州市",
        district = "姑苏区",
        latitude = 31.2989,
        longitude = 120.5853,
        rating = 4.8,
        priceYuanPerDay = 720,
        priceLabel = "¥720/天",
    ),
    Guide(
        id = "7",
        name = "马东",
        cityDisplay = "上海市 · 黄浦区",
        province = "上海市",
        city = "市辖区",
        district = "黄浦区",
        latitude = 31.2304,
        longitude = 121.4737,
        rating = 4.2,
        priceYuanPerDay = 980,
        priceLabel = "¥980/天",
    ),
    Guide(
        id = "8",
        name = "韩雪",
        cityDisplay = "北京市 · 朝阳区",
        province = "北京市",
        city = "市辖区",
        district = "朝阳区",
        latitude = 39.9219,
        longitude = 116.4432,
        rating = 4.5,
        priceYuanPerDay = 850,
        priceLabel = "¥850/天",
    ),
    // 贴近模拟坐标（杭州），便于「按距离 / 按好评」小半径档位有数据
    Guide(
        id = "9",
        name = "沈清",
        cityDisplay = "杭州市 · 西湖区",
        province = "浙江省",
        city = "杭州市",
        district = "西湖区",
        latitude = 30.27435,
        longitude = 120.15525,
        rating = 4.8,
        priceYuanPerDay = 610,
        priceLabel = "¥610/天",
    ),
    Guide(
        id = "10",
        name = "郑一",
        cityDisplay = "杭州市 · 拱墅区",
        province = "浙江省",
        city = "杭州市",
        district = "拱墅区",
        latitude = 30.2751,
        longitude = 120.1546,
        rating = 4.1,
        priceYuanPerDay = 430,
        priceLabel = "¥430/天",
    ),
)
