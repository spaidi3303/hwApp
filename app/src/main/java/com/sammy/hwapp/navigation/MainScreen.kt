package com.sammy.hwapp.navigation

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen(currentDate: String) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationMenu(navController = navController) }
    ) {
        it
        NavGraph(navHostController = navController, currentDate)
    }
}