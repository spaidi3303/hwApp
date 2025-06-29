package com.sammy.hwapp.Activity.Register

import android.content.Context.MODE_PRIVATE
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.navigation.NavHostController
import com.montanainc.simpleloginscreen.components.ClassSelectorComponent
import com.montanainc.simpleloginscreen.components.HeadingTextComponent
import com.montanainc.simpleloginscreen.components.MyTextFieldComponent
import com.montanainc.simpleloginscreen.components.NormalTextComponent
import com.montanainc.simpleloginscreen.components.PasswordTextFieldComponent
import com.montanainc.simpleloginscreen.components.RegisterButton
import com.sammy.hwapp.splash.DataLoader
import com.sammy.hwapp.splash.LogIo.checkDiaries
import com.sammy.hwapp.splash.LogIo.registerUser

@Composable
fun RegisterActivity(navHostController: NavHostController) {
    var login by remember { mutableStateOf("") }
    var loginDn by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var passwordDn by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf<String?>(null) }
    var loginDnError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var passwordDnError by remember { mutableStateOf<String?>(null) }
    var selectedClass by remember { mutableStateOf("") }
    var classError by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    var isButtonEnabled by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
            NormalTextComponent(value = "Мир Ти,")
            HeadingTextComponent(value = "Создай Аккаунт")
            Spacer(modifier = Modifier.height(25.dp))

            Column {
                MyTextFieldComponent(
                    labelValue = "Логин",
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
                    labelValue = "Пароль",
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
                MyTextFieldComponent(
                    labelValue = "Логин дневника",
                    icon = Icons.Outlined.Person,
                    textValue = loginDn,
                    onValueChange = {
                        loginDn = it
                        loginDnError = null
                    },
                    isError = showError && loginDn.isBlank()
                )
                loginDnError?.let {
                    Text(it, color = Color.Red, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(10.dp))
                PasswordTextFieldComponent(
                    labelValue = "Пароль дневника",
                    icon = Icons.Outlined.Lock,
                    textValue = passwordDn,
                    onValueChange = {
                        passwordDn = it
                        passwordDnError = null
                    },
                    isError = showError && passwordDn.isBlank()
                )
                ClassSelectorComponent(
                    selectedClass = selectedClass,
                    onClassSelected = { selectedClass = it },
                    icon = Icons.Outlined.School,
                    isError = showError && selectedClass.isBlank()
                )
                Spacer(modifier = Modifier.weight(1f))
                RegisterButton(
                    onClick = {
                        when {
                            login.isBlank() || password.isBlank() || loginDn.isBlank() || passwordDn.isBlank() || selectedClass.isBlank() -> {
                                showError = true
                                loginError =
                                    if (login.isBlank()) "Введите логин" else null
                                loginDnError =
                                    if (loginDn.isBlank()) "Введите логин от дневника" else null
                                passwordError =
                                    if (password.isBlank()) "Введите пароль" else null
                                passwordDnError =
                                    if (passwordDn.isBlank()) "Введите пароль от дневника" else null
                                classError =
                                    if (selectedClass.isBlank()) "Выберите класс" else null
                                return@RegisterButton
                            }

                            login.length < 3 || password.length < 6 -> {
                                showError = true
                                loginError =
                                    if (login.length < 3) "Логин слишком короткий" else null
                                passwordError =
                                    if (password.length < 6) "Пароль слишком простой" else null
                                return@RegisterButton
                            }

                            !login.matches(Regex("^[a-zA-Z]+\$")) -> {
                                loginError =
                                    "Логин должен содержать только английские буквы"
                                return@RegisterButton
                            }

                            !password.matches(Regex("^[a-zA-Z0-9]+\$")) -> {
                                passwordError =
                                    "Пароль должен содержать только латинские буквы и цифры"
                                return@RegisterButton
                            }
                        }

                        showError = false
                        CheckDataDiaries(
                            login = login,
                            password = password,
                            loginDnevnik = loginDn,
                            passwordDnevnik = passwordDn,
                            selectedClass = selectedClass,
                            navHostController = navHostController,
                            context = context,
                            errorMessage = {errorMessage = it},
                            onErrorDnLogin = { loginDnError = it },
                            onErrorDnPassword = { passwordDnError = it },
                            isButtonEnabled
                        )
                    },
                    isEnabled = isButtonEnabled,
                    errorMessage = errorMessage,
                    navHostController
                )
//
            }
        }
    }
}

fun CheckDataDiaries(
    login: String,
    password: String,
    loginDnevnik: String,
    passwordDnevnik: String,
    selectedClass: String,
    navHostController: NavHostController,
    context: android.content.Context,
    errorMessage: (String) -> Unit,
    onErrorDnLogin: (String) -> Unit,
    onErrorDnPassword: (String) -> Unit,
    isEnabled: Boolean
) {

    checkDiaries(loginDnevnik, passwordDnevnik) { result ->
        val status = result?.toInt()
        val activity = context as? android.app.Activity
        if (status == 0) {
            activity?.runOnUiThread {
                onErrorDnLogin("Логин или пароль от дневника неверны")
                onErrorDnPassword("Логин или пароль от дневника неверны")
                return@runOnUiThread
            }
        } else {
            registerUser(
                login,
                password,
                selectedClass,
                loginDnevnik,
                passwordDnevnik
            ) { result ->
                activity?.runOnUiThread {
                    val statusReg = result?.toBoolean() == true
                    if (!statusReg) {
                        errorMessage("Произошла ошибка")
                    } else {
                        val sharedPref = context.getSharedPreferences("UserData", MODE_PRIVATE)
                        sharedPref.edit {
                            putString("login", login)
                            putString("password", password)
                            putString("class", selectedClass)
                            putString("loginDn", loginDnevnik)
                            putString("passwordDn", passwordDnevnik)
                            apply()
                        }
                        DataLoader.loader(context) {
                            navHostController.navigate("Main") {
                                popUpTo("Register") { inclusive = true }
                            }
                        }
                    }
                }
            }
        }
    }
}
