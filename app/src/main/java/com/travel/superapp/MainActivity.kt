package com.travel.superapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.travel.superapp.ui.App
import com.travel.superapp.ui.theme.TravelSuperAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TravelSuperAppTheme {
                App()
            }
        }
    }
}

