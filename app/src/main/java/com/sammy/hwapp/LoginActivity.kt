package com.sammy.hwapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sammy.hwapp.LogIo.LogIo.getDatas
import com.sammy.hwapp.LogIo.LogIo.loginUser
import com.sammy.hwapp.databinding.ActivityLoginBinding
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val enterLogin = binding.enterLogin
        val enterPassword = binding.enterPassword
        val lgButton = binding.lgButton
        val regButton = binding.regButton

        lgButton.setOnClickListener {
            val login = enterLogin.text.toString()
            val password = enterPassword.text.toString()
            when {
                login.isEmpty() || password.isEmpty() -> {
                    Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    loginUser(login, password) { result ->
                        runOnUiThread {
                            val status = result?.toInt()

                            when (status) {
                                0 -> {
                                    enterLogin.error = "Пользователь не найден"
                                    regButton.visibility = Button.VISIBLE

                                }

                                1 -> {
                                    enterPassword.error = "Неверный пароль"
                                }

                                2 -> {
                                    getDatas(login) { result ->
                                        runOnUiThread {
                                            if (result != null) {
                                                val json = JSONObject(result)
                                                val className = json.getString("class")
                                                val loginDn = json.getString("login_dn")
                                                val passwordDn = json.getString("password_dn")


                                                val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
                                                sharedPref.edit {
                                                    putString("login", login)
                                                    putString("password", password)
                                                    putString("class", className)
                                                    putString("loginDn", loginDn)
                                                    putString("passwordDn", passwordDn)
                                                    apply()
                                                }

                                                DataLoader.loadMarks(this) {
                                                    finish()
                                                    val i = Intent(
                                                        this@LoginActivity,
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

        regButton.setOnClickListener {
            finish()
            val i = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(i)
        }
    }
}