package com.sberkids.ht

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sberkids.ht.ui.MainScreen
import com.sberkids.ht.ui.theme.SberKidsHtTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            SberKidsHtTheme {
                MainScreen()
            }
        }
    }
}
