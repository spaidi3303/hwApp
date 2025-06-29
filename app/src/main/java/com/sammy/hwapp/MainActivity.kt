package com.sammy.hwapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.sammy.hwapp.Activity.ScreenControl
import com.sammy.hwapp.splash.DataLoader

class MainActivity : ComponentActivity() {
    private val isLoading = mutableStateOf(true)

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        installSplashScreen().setKeepOnScreenCondition {
            isLoading.value
        }

        DataLoader.loader(this) {
            isLoading.value = false
        }

        setContent {
            ScreenControl()
        }
    }
}
