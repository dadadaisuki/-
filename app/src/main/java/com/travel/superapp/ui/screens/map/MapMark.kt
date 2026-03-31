package com.travel.superapp.ui.screens.map

import java.util.UUID

/** 标记分类 */
enum class MarkCategory(val label: String, val emoji: String) {
    Food("美食", "🍜"),
    Restaurant("饭馆", "🍽️"),
    Landmark("地标", "🏛️"),
    ScenicSpot("景点", "🏞️"),
    Shopping("购物", "🛍️"),
    Custom("自定义", "📍"),
}

/** 单个地图标记 */
data class MapMark(
    val id: String = UUID.randomUUID().toString(),
    val lat: Double,
    val lon: Double,
    val category: MarkCategory,
    val title: String,
    val note: String = "",
    val photos: List<String> = emptyList(), // 本地路径或网络 URL
    val createdAt: Long = System.currentTimeMillis(),
    val authorId: String = "",
    val authorName: String = "",
)

/** 地图范围 */
data class MapBounds(
    val minLat: Double,
    val minLon: Double,
    val maxLat: Double,
    val maxLon: Double,
)