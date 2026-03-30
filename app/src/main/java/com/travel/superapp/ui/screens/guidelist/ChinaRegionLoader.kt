package com.travel.superapp.ui.screens.guidelist

import android.content.res.AssetManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import org.json.JSONArray
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

/**
 * 全国省 / 市 / 区（县）树，数据来自 modood/Administrative-divisions-of-China 的 pca-code.json。
 */
data class ChinaRegionTree(
    val provinces: List<String>,
    val citiesByProvince: Map<String, List<String>>,
    val districtsByProvinceCity: Map<String, List<String>>,
)

internal fun provinceCityKey(province: String, city: String): String = "$province|$city"

fun loadChinaRegionTreeFromJson(jsonText: String): ChinaRegionTree {
    val root = JSONArray(jsonText)
    val provinces = mutableListOf<String>()
    val citiesByProvince = linkedMapOf<String, MutableList<String>>()
    val districtsByProvinceCity = linkedMapOf<String, MutableList<String>>()

    for (i in 0 until root.length()) {
        val prov = root.getJSONObject(i)
        val provinceName = prov.getString("name")
        provinces.add(provinceName)
        val citiesArr = prov.getJSONArray("children")
        val cityList = mutableListOf<String>()
        for (j in 0 until citiesArr.length()) {
            val cityObj = citiesArr.getJSONObject(j)
            val cityName = cityObj.getString("name")
            cityList.add(cityName)
            val districtsArr = cityObj.optJSONArray("children")
            val districtList = mutableListOf<String>()
            if (districtsArr != null) {
                for (k in 0 until districtsArr.length()) {
                    val distObj = districtsArr.getJSONObject(k)
                    districtList.add(distObj.getString("name"))
                }
            }
            districtsByProvinceCity[provinceCityKey(provinceName, cityName)] = districtList
        }
        citiesByProvince[provinceName] = cityList
    }

    return ChinaRegionTree(
        provinces = provinces.toList(),
        citiesByProvince = citiesByProvince.mapValues { it.value.toList() },
        districtsByProvinceCity = districtsByProvinceCity.mapValues { it.value.toList() },
    )
}

fun ChinaRegionTree.citiesForProvince(province: String?): List<String> =
    if (province == null) emptyList()
    else citiesByProvince[province].orEmpty()

fun ChinaRegionTree.districtsForProvinceCity(province: String?, city: String?): List<String> =
    if (province == null || city == null) emptyList()
    else districtsByProvinceCity[provinceCityKey(province, city)].orEmpty()

fun loadChinaRegionTreeFromAssets(assets: AssetManager): ChinaRegionTree {
    assets.open("china_pca_regions.json").use { input ->
        InputStreamReader(input, StandardCharsets.UTF_8).use { reader ->
            return loadChinaRegionTreeFromJson(reader.readText())
        }
    }
}

@Composable
fun rememberChinaRegions(): ChinaRegionTree {
    val context = LocalContext.current
    return remember {
        loadChinaRegionTreeFromAssets(context.assets)
    }
}
