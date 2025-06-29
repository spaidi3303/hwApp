package com.sammy.hwapp.navigation

import com.sammy.hwapp.R

sealed class BottomItemMy (
    val title: String,
    val iconId: Int,
    val route: String
) {
    object HwScreen: BottomItemMy("Дз", R.drawable.baseline_book_24, "hw_screen")
    object Grades: BottomItemMy("Оценки", R.drawable.grades, "grades_screen")
    object AllGrades: BottomItemMy("Все оценки", R.drawable.all_grades, "all_grades_screen")

}