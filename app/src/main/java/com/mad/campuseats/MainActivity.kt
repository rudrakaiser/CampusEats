package com.mad.campuseats

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mad.campuseats.navigation.CampusEatsApp
import com.mad.campuseats.ui.theme.CampusEatsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CampusEatsTheme {
                CampusEatsApp()
            }
        }
    }
}
