package com.sammy.hwapp.Activity

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController

@Composable
fun ScreenControl() {
    val navController = rememberNavController()
    NavActiv(navHostController = navController)
}