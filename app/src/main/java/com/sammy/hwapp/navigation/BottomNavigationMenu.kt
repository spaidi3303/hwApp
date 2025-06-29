package com.sammy.hwapp.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController

@Composable
fun BottomNavigationMenu (navController: NavController) {
    val listItems = listOf(BottomItemMy.HwScreen, BottomItemMy.Grades, BottomItemMy.AllGrades)

    NavigationBar(){
        var currentRoute = navController.currentBackStackEntry?.destination?.route
        listItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { navController.navigate(item.route) },
                icon = { Icon(painter = painterResource(id = item.iconId), contentDescription = "Icon") },
                label = { Text(text = item.title) }
            )

        }
    }
}