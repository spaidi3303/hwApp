package com.sammy.hwapp.Activity

import android.content.Context.MODE_PRIVATE
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sammy.hwapp.Activity.Login.LoginActivity
import com.sammy.hwapp.Activity.Main.MainSActivity
import com.sammy.hwapp.Activity.Register.RegisterActivity
import com.sammy.hwapp.MainActivity
import com.sammy.hwapp.splash.LogIo.loginUser

@Composable
fun NavActiv(navHostController: NavHostController) {
    NavHost(navController = navHostController, startDestination = "Login") {
        composable("Login", content = { LoginActivity(navHostController) })
        composable("Register", content = { RegisterActivity(navHostController) })
        composable("Main", content = { MainSActivity(navHostController) })
    }
    CheckLogin(navHostController)
}

@Composable
private fun CheckLogin(navHostController: NavHostController) {
    val context = LocalContext.current
    val activity = (context as? MainActivity)
    val sharedPref = activity!!.getSharedPreferences("UserData", MODE_PRIVATE)
    if (sharedPref.contains("login")) {
        val login = sharedPref.getString("login", "").toString()
        val password = sharedPref.getString("password", "").toString()

        loginUser(login, password) { result ->
            context.runOnUiThread {
                val status = result?.toIntOrNull()
                if (status == 2) {
                    navHostController.navigate("Main")
                } else {
                    navHostController.navigate("Login")
                }
            }
        }
    } else {
        navHostController.navigate("Login")
    }
}