package com.travel.superapp.ui.screens.map

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

/** 路径轨迹仓库：本地存储 + 后端发布接口预留 */
class RouteRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("route_tracks", Context.MODE_PRIVATE)
    private val _tracks = MutableStateFlow<List<RouteTrack>>(emptyList())
    val tracks: StateFlow<List<RouteTrack>> = _tracks.asStateFlow()

    init {
        loadFromDisk()
    }

    private fun loadFromDisk() {
        val json = prefs.getString("tracks_json", null) ?: return
        try {
            val array = JSONArray(json)
            val list = mutableListOf<RouteTrack>()
            for (i in 0 until array.length()) {
                list.add(parseTrack(array.getJSONObject(i)))
            }
            _tracks.value = list
        } catch (_: Exception) {
            _tracks.value = emptyList()
        }
    }

    private fun saveToDisk() {
        val array = JSONArray()
        _tracks.value.forEach { track ->
            array.put(toJson(track))
        }
        prefs.edit().putString("tracks_json", array.toString()).apply()
    }

    fun saveTrack(track: RouteTrack) {
        val existing = _tracks.value.indexOfFirst { it.id == track.id }
        _tracks.value = if (existing >= 0) {
            _tracks.value.toMutableList().also { it[existing] = track }
        } else {
            _tracks.value + track
        }
        saveToDisk()
    }

    fun deleteTrack(trackId: String) {
        _tracks.value = _tracks.value.filter { it.id != trackId }
        saveToDisk()
    }

    fun getTrackById(trackId: String): RouteTrack? {
        return _tracks.value.find { it.id == trackId }
    }

    fun markPublished(trackId: String) {
        _tracks.value = _tracks.value.map {
            if (it.id == trackId) it.copy(isPublished = true) else it
        }
        saveToDisk()
    }

    // ─── 后端发布接口预留 ───────────────────────────────────────────────
    suspend fun publishTrack(trackId: String): Boolean {
        val track = getTrackById(trackId) ?: return false
        // TODO: 调用后端 API POST /api/routes/publish
        // 返回 true 表示发布成功，同时本地标记为已发布
        markPublished(trackId)
        return true
    }

    suspend fun fetchCommunityRoutes(): List<RouteTrack> {
        // TODO: GET /api/routes/community
        return emptyList()
    }

    // ─── 序列化 ─────────────────────────────────────────────────────────
    private fun toJson(t: RouteTrack): JSONObject = JSONObject().apply {
        put("id", t.id)
        put("name", t.name)
        put("points", JSONArray().apply {
            t.points.forEach { pt ->
                put(JSONObject().apply {
                    put("lat", pt.lat)
                    put("lon", pt.lon)
                    put("altitude", pt.altitude)
                    put("speedMps", pt.speedMps.toDouble())
                    put("timestamp", pt.timestamp)
                    put("accuracy", pt.accuracy.toDouble())
                })
            }
        })
        put("totalDistanceMeters", t.totalDistanceMeters)
        put("totalDurationMs", t.totalDurationMs)
        put("createdAt", t.createdAt)
        put("authorName", t.authorName)
        put("isPublished", t.isPublished)
        put("coverImagePath", t.coverImagePath ?: JSONObject.NULL)
        put("tags", JSONArray(t.tags))
    }

    private fun parseTrack(obj: JSONObject): RouteTrack {
        val pointsArray = obj.optJSONArray("points") ?: JSONArray()
        val points = (0 until pointsArray.length()).map { i ->
            val p = pointsArray.getJSONObject(i)
            TrackPoint(
                lat = p.getDouble("lat"),
                lon = p.getDouble("lon"),
                altitude = p.optDouble("altitude", 0.0),
                speedMps = p.optDouble("speedMps", 0.0).toFloat(),
                timestamp = p.optLong("timestamp", System.currentTimeMillis()),
                accuracy = p.optDouble("accuracy", 0.0).toFloat(),
            )
        }
        return RouteTrack(
            id = obj.getString("id"),
            name = obj.optString("name", ""),
            points = points,
            totalDistanceMeters = obj.optDouble("totalDistanceMeters", 0.0),
            totalDurationMs = obj.optLong("totalDurationMs", 0L),
            createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
            authorName = obj.optString("authorName", "游客"),
            isPublished = obj.optBoolean("isPublished", false),
            coverImagePath = obj.optString("coverImagePath", "").takeIf { it.isNotEmpty() && it != "null" },
            tags = (0 until (obj.optJSONArray("tags")?.length() ?: 0)).map { idx ->
                obj.getJSONArray("tags").getString(idx)
            },
        )
    }

    companion object {
        @Volatile
        private var INSTANCE: RouteRepository? = null

        fun getInstance(context: Context): RouteRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: RouteRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
