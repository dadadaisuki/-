package com.travel.superapp.ui.screens.map

import java.util.UUID
import kotlin.math.*

/** 一条路径轨迹 */
data class RouteTrack(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val points: List<TrackPoint> = emptyList(),
    val totalDistanceMeters: Double = 0.0,
    val totalDurationMs: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val authorName: String = "游客",
    val isPublished: Boolean = false,
    val coverImagePath: String? = null,
    val tags: List<String> = emptyList(),
)

/** 轨迹中的单个 GPS 点 */
data class TrackPoint(
    val lat: Double,
    val lon: Double,
    val altitude: Double = 0.0,
    val speedMps: Float = 0f,
    val timestamp: Long = System.currentTimeMillis(),
    val accuracy: Float = 0f,
)

/** 路径分享/发布格式 */
data class RouteShareInfo(
    val routeId: String,
    val title: String,
    val distance: String,
    val duration: String,
    val summary: String,
    val tags: List<String>,
)

fun calculateDistance(points: List<TrackPoint>): Double {
    if (points.size < 2) return 0.0
    var total = 0.0
    for (i in 1 until points.size) {
        total += haversine(
            points[i - 1].lat, points[i - 1].lon,
            points[i].lat, points[i].lon,
        )
    }
    return total
}

/** Haversine 公式计算两点间球面距离（米） */
fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6371000.0 // 地球半径（米）
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return R * c
}

/** 格式化距离 */
fun formatDistance(meters: Double): String = when {
    meters < 1000 -> "%.0f 米".format(meters)
    else -> "%.2f 公里".format(meters / 1000)
}

/** 格式化时长 */
fun formatDuration(ms: Long): String {
    val totalSec = ms / 1000
    val hours = totalSec / 3600
    val minutes = (totalSec % 3600) / 60
    val seconds = totalSec % 60
    return if (hours > 0) {
        "%d小时%d分".format(hours, minutes)
    } else if (minutes > 0) {
        "%d分%d秒".format(minutes, seconds)
    } else {
        "%d秒".format(seconds)
    }
}
