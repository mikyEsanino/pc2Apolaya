package com.example.pc2apolaya

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import com.example.pc2apolaya.presentation.navigation.NavigationMenu
import com.example.pc2apolaya.ui.theme.Pc2ApolayaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Pc2ApolayaTheme {
                NavigationMenu()
            }
        }
    }
}

