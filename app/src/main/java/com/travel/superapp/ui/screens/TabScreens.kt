package com.travel.superapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AiScreen(contentPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
    ) {
        Text("问AI（前端占位）")
        Text("后续接入 DeepSeek：对话、行程规划、景点问答等。")
    }
}

@Composable
fun PostScreen(contentPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
    ) {
        Text("投稿（前端占位）")
        Text("后续会在这里做：图文发布、定位、标签、草稿箱。")
    }
}

@Composable
fun MineScreen(contentPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
    ) {
        Text("我的（前端占位）")
        Text("后续会在这里做：游客/导游身份、培训与测试、资料审核等。")
    }
}

