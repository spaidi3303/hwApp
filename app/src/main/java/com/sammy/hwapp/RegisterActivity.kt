package com.sammy.hwapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sammy.hwapp.LogIo.LogIo.checkDiaries
import com.sammy.hwapp.LogIo.LogIo.loginUser
import com.sammy.hwapp.LogIo.LogIo.registerUser
import com.sammy.hwapp.LoginActivity
import org.json.JSONObject
import kotlin.math.log
import androidx.core.content.edit

class RegisterActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        var etUsername: EditText = findViewById(R.id.etUsername)
        var etPassword: EditText = findViewById(R.id.etPassword)
        var classSpinner: Spinner = findViewById(R.id.classSpinner)
        var btnRegister: Button = findViewById(R.id.btnRegister)
        var etLoginDn: EditText = findViewById(R.id.etLoginDnevnik)
        var etPasswordDn: EditText = findViewById(R.id.etPasswordDnevnik)

        val classList = listOf("10A", "10B", "10V")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, classList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        classSpinner.adapter = adapter

        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val login = etLoginDn.text.toString().trim()
            val passw = etPasswordDn.text.toString().trim()
            val selectedClass = classSpinner.selectedItem.toString()

            when {
                username.isEmpty() || password.isEmpty() || login.isEmpty() || passw.isEmpty() -> {
                    Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT)
                        .show()
                }

                !username.matches(Regex("^[a-zA-Z]+\$")) -> {
                    etUsername.error = "Логин должен содержать только английские буквы"
                }

                username.length >= 20 || username.length < 5 -> {
                    etUsername.error = "Логин должен быть от 5 до 20 символов"
                }

                !password.matches(Regex("^[a-zA-Z0-9]+\$")) -> {
                    etPassword.error =
                        "Пароль должен содержать только латинские буквы и цифры"
                }

                password.length < 8 -> {
                    etPassword.error = "Пароль слишком короткий!"
                }

                else -> {
                    btnRegister.isEnabled = false
                    checkDiaries(login, passw) { result, error ->
                        runOnUiThread {
                            val status = result?.toInt()
                            if (status == 0) {

                                etLoginDn.error = "Не верен"
                                etPasswordDn.error = "Не верен"
                                btnRegister.isEnabled = true
                                return@runOnUiThread
                            } else {

                                registerUser(
                                    username,
                                    password,
                                    selectedClass,
                                    login,
                                    passw
                                ) { result, error ->
                                    runOnUiThread {
                                        val status = result?.toBoolean() ?: false
                                        if (!status) {
                                            btnRegister.error = "Произошла ошибка!"
                                            btnRegister.isEnabled = true
                                        } else {
                                            // Сохранение данных
                                            val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
                                            sharedPref.edit {
                                                putString("login", username)
                                                putString("password", password)
                                                putString("class", selectedClass)
                                                putString("loginDn", login)
                                                putString("passwordDn", passw)
                                                apply()
                                            }
                                            DataLoader.loadMarks(this) {
                                                finish()
                                                val i = Intent(
                                                    this@RegisterActivity,
                                                    MainActivity::class.java
                                                )
                                                startActivity(i)
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
    }
}