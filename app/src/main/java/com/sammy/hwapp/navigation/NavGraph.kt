package com.sammy.hwapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sammy.hwapp.Screens.AllGradesScreen
import com.sammy.hwapp.Screens.GradesScreen
import com.sammy.hwapp.Screens.HwScreen

@Composable
fun NavGraph(navHostController: NavHostController, currentDate: String) {
    NavHost(navController = navHostController, startDestination = "hw_screen") {
        composable("hw_screen") { HwScreen(currentDate = currentDate) }
        composable("grades_screen") { GradesScreen() }
        composable("all_grades_screen") { AllGradesScreen() }
    }
}
