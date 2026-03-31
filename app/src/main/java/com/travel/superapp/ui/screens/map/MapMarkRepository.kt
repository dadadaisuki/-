package com.travel.superapp.ui.screens.map

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

/** 地图标记仓库：本地 SharedPreferences 存储 + 后端接口预留 */
class MapMarkRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("map_marks", Context.MODE_PRIVATE)
    private val _marks = MutableStateFlow<List<MapMark>>(emptyList())
    val marks: StateFlow<List<MapMark>> = _marks.asStateFlow()

    init {
        loadFromDisk()
    }

    private fun loadFromDisk() {
        val json = prefs.getString("marks_json", null) ?: return
        try {
            val array = JSONArray(json)
            val list = mutableListOf<MapMark>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(parseMark(obj))
            }
            _marks.value = list
        } catch (_: Exception) {
            _marks.value = emptyList()
        }
    }

    private fun saveToDisk() {
        val array = JSONArray()
        _marks.value.forEach { mark ->
            array.put(toJson(mark))
        }
        prefs.edit().putString("marks_json", array.toString()).apply()
    }

    fun addMark(mark: MapMark) {
        _marks.value = _marks.value + mark
        saveToDisk()
    }

    fun updateMark(mark: MapMark) {
        _marks.value = _marks.value.map { if (it.id == mark.id) mark else it }
        saveToDisk()
    }

    fun deleteMark(markId: String) {
        _marks.value = _marks.value.filter { it.id != markId }
        saveToDisk()
    }

    // ─── 后端接口预留（TODO: 实现网络同步） ────────────────────────────────
    suspend fun syncFromServer(): List<MapMark> {
        // TODO: 调用后端 API GET /api/map/marks，合并到 _marks
        return _marks.value
    }

    suspend fun pushToServer(mark: MapMark) {
        // TODO: 调用后端 API POST /api/map/marks
    }

    // ─── 工具 ────────────────────────────────────────────────────────────
    private fun toJson(m: MapMark): JSONObject = JSONObject().apply {
        put("id", m.id)
        put("lat", m.lat)
        put("lon", m.lon)
        put("category", m.category.name)
        put("title", m.title)
        put("note", m.note)
        put("photos", JSONArray(m.photos))
        put("createdAt", m.createdAt)
        put("authorId", m.authorId)
        put("authorName", m.authorName)
    }

    private fun parseMark(obj: JSONObject): MapMark = MapMark(
        id = obj.getString("id"),
        lat = obj.getDouble("lat"),
        lon = obj.getDouble("lon"),
        category = MarkCategory.valueOf(obj.optString("category", "Custom")),
        title = obj.getString("title"),
        note = obj.optString("note", ""),
        photos = (0 until (obj.optJSONArray("photos")?.length() ?: 0)).map { idx ->
            obj.getJSONArray("photos").getString(idx)
        },
        createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
        authorId = obj.optString("authorId", ""),
        authorName = obj.optString("authorName", ""),
    )

    companion object {
        @Volatile
        private var INSTANCE: MapMarkRepository? = null

        fun getInstance(context: Context): MapMarkRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MapMarkRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
