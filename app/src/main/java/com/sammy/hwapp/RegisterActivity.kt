package com.sammy.hwapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sammy.hwapp.LogIo.LogIo.checkDiaries
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.sammy.hwapp.LoginActivity
import com.sammy.hwapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val classSpinner = binding.classSpinner
        val classList = listOf("10A", "10B", "10V")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, classList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        classSpinner.adapter = adapter

        binding.btnRegister.setOnClickListener {
            register()
        }
    }

    private fun register() {
        val email = binding.editTextEmail.text.toString().trim()
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val loginDnevnik = binding.etLoginDnevnik.text.toString().trim()
        val passwordDnevnik = binding.etPasswordDnevnik.text.toString().trim()
        val selectedClass = binding.classSpinner.selectedItem.toString()
        when {
            username.isEmpty() || password.isEmpty() || loginDnevnik.isEmpty() || passwordDnevnik.isEmpty() -> {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT)
                    .show()
                return
            }

            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.editTextEmail.error = "Неверный формат почты"
                return
            }

            !username.matches(Regex("^[a-zA-Z]+\$")) -> {
                binding.etUsername.error = "Логин должен содержать только английские буквы"
                return
            }

            username.length >= 20 || username.length < 5 -> {
                binding.etUsername.error = "Логин должен быть от 5 до 20 символов"
                return
            }

            !password.matches(Regex("^[a-zA-Z0-9]+\$")) -> {
                binding.etPassword.error =
                    "Пароль должен содержать только латинские буквы и цифры"
                return
            }

            password.length < 8 -> {
                binding.etPassword.error = "Пароль слишком короткий!"
                return
            }
        }

        binding.btnRegister.isEnabled = false


        checkDiaries(loginDnevnik, passwordDnevnik) { result ->
            runOnUiThread {
                val status = result?.toInt()
                if (status == 0) {
                    binding.etLoginDnevnik.error = "Логин или пароль от дневника неверны"
                    binding.etPasswordDnevnik.error = "Логин или пароль от дневника неверны"
                    binding.btnRegister.isEnabled = true
                    return@runOnUiThread
                } else {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val user: FirebaseUser? = auth.currentUser
                                if (user != null) {
                                    saveAdditionalUserData(
                                        user.uid,
                                        username,
                                        email,
                                        loginDnevnik,
                                        passwordDnevnik,
                                        selectedClass
                                    )
                                } else {
                                    binding.btnRegister.isEnabled = true
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Ошибка: Пользователь не найден после регистрации.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } else {
                                binding.btnRegister.isEnabled = true
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Ошибка регистрации: ${task.exception?.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                }
            }
        }
    }

    private fun saveAdditionalUserData(
        userId: String,
        username: String,
        email: String,
        diaryLogin: String,
        diaryPassword: String,
        userClass: String
    ) {
        val userMap = hashMapOf(
            "username" to username,
            "email" to email,
            "diaryLogin" to diaryLogin,
            "diaryPassword" to diaryPassword,
            "class" to userClass
        )
        db.collection("users").document(userId)
            .set(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Регистрация успешна! Данные сохранены.",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                    finish()
                    DataLoader.loadMarks(this) {
                        finish()
                        startActivity(Intent(
                            this@RegisterActivity,
                            MainActivity::class.java
                        ))
                    }
                } else {
                    val errorMessage = task.exception?.message ?: "Неизвестная ошибка."
                    Toast.makeText(
                        this@RegisterActivity,
                        "Ошибка СОХРАНЕНИЯ ДАННЫХ В FIRESTORE: $errorMessage",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.btnRegister.isEnabled = true // Включаем кнопку
                }
            }
    }
}
