package com.sammy.hwapp.Activity.Login

import android.content.Context.MODE_PRIVATE
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.navigation.NavHostController
import com.montanainc.simpleloginscreen.components.HeadingTextComponent
import com.montanainc.simpleloginscreen.components.MyTextFieldComponent
import com.montanainc.simpleloginscreen.components.NormalTextComponent
import com.montanainc.simpleloginscreen.components.PasswordTextFieldComponent
import com.montanainc.simpleloginscreen.ui.theme.AccentColor
import com.montanainc.simpleloginscreen.ui.theme.Secondary
import com.montanainc.simpleloginscreen.ui.theme.TextColor
import com.sammy.hwapp.splash.DataLoader
import com.sammy.hwapp.splash.LogIo.getDatas
import com.sammy.hwapp.splash.LogIo.loginUser
import org.json.JSONObject

@Composable
fun LoginActivity(navController: NavHostController) {
    val context = LocalContext.current
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(28.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column {
                NormalTextComponent(value = "Hey, thee")
                HeadingTextComponent(value = "Welcome Back")
            }

            Spacer(modifier = Modifier.height(25.dp))

            Column {
                MyTextFieldComponent(
                    labelValue = "Login",
                    icon = Icons.Outlined.Person,
                    textValue = login,
                    onValueChange = {
                        login = it
                        loginError = null
                    },
                    isError = showError && login.isBlank()
                )
                loginError?.let {
                    Text(it, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                PasswordTextFieldComponent(
                    labelValue = "Password",
                    icon = Icons.Outlined.Lock,
                    textValue = password,
                    onValueChange = {
                        password = it
                        passwordError = null
                    },
                    isError = showError && password.isBlank()
                )
                passwordError?.let {
                    Text(it, color = Color.Red, fontSize = 12.sp)
                }
            }


            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                brush = Brush.horizontalGradient(listOf(Secondary, AccentColor)),
                                shape = RoundedCornerShape(50.dp)
                            )
                            .fillMaxWidth(0.8f)
                            .clickable {
                                if (login.isBlank() || password.isBlank() || loginError != null || passwordError != null) {
                                    showError = true
                                    loginError = if (login.isBlank()) "Введите логин" else null
                                    passwordError = if (password.isBlank()) "Введите пароль" else null
                                } else if (login.length < 3 || password.length < 6) {
                                    showError = true
                                    loginError = if (login.length < 3) "Логин слишком короткий" else null
                                    passwordError = if (password.length < 6) "Пароль слишком простой" else null
                                } else {
                                    showError = false
                                    CheckData(
                                        login = login,
                                        password = password,
                                        navController = navController,
                                        onErrorLogin = { loginError = it },
                                        onErrorPassword = { passwordError = it },
                                        context = context
                                    )
                                }
                            }
                            .heightIn(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Login", color = Color.White, fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Row {
                        Text(
                            text = "Нет аккаунта? ",
                            style = TextStyle(color = TextColor, fontSize = 15.sp)
                        )
                        Text(
                            text = "Зарегистрироваться",
                            style = TextStyle(color = Secondary, fontSize = 15.sp),
                            modifier = Modifier.clickable {
                                navController.navigate("Register")
                            }
                        )
                    }
                }
            }
        }
    }
}

fun CheckData(
    login: String,
    password: String,
    navController: NavHostController,
    onErrorLogin: (String) -> Unit,
    onErrorPassword: (String) -> Unit,
    context: android.content.Context
) {
    loginUser(login, password) { result ->
        val status = result?.toInt()
        val activity = context as? android.app.Activity

        when (status) {
            0 -> {
                activity?.runOnUiThread {
                    onErrorLogin("Пользователь не найден")
                }
            }

            1 -> {
                activity?.runOnUiThread {
                    onErrorPassword("Неверный пароль")
                }
            }

            2 -> {
                getDatas(login) { result ->
                    if (result != null) {
                        val json = JSONObject(result)
                        val className = json.getString("class")
                        val loginDn = json.getString("login_dn")
                        val passwordDn = json.getString("password_dn")

                        activity?.runOnUiThread {
                            val sharedPref = context.getSharedPreferences("UserData", MODE_PRIVATE)
                            sharedPref.edit {
                                putString("login", login)
                                putString("password", password)
                                putString("class", className)
                                putString("loginDn", loginDn)
                                putString("passwordDn", passwordDn)
                                apply()
                            }
                            DataLoader.loader(context) {
                                activity.runOnUiThread {
                                    navController.navigate("Main") {
                                        popUpTo("Register") { inclusive = true }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
