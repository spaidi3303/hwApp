package com.sammy.hwapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.sammy.hwapp.LogIo.LogIo.getDatas
import com.sammy.hwapp.LogIo.LogIo.loginUser
import com.sammy.hwapp.RegisterActivity
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

        val enterEmail = binding.editTextEmail
        val enterPassword = binding.enterPassword
        val lgButton = binding.lgButton
        val regButton = binding.regButton

        lgButton.setOnClickListener {
            val email = enterEmail.text.toString().trim()
            val password = enterPassword.text.toString()
            val auth = FirebaseAuth.getInstance()

            val db = FirebaseFirestore.getInstance()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.editTextEmail.error = "Неверный формат почты"
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Неверный пользователь или пароль!",
                            Toast.LENGTH_SHORT
                        ).show()
                        enterEmail.error = "Пользователь не найден"
                        regButton.visibility = Button.VISIBLE
                        enterPassword.error = "Неверный пароль"
                        return@addOnCompleteListener
                    } else {
                        binding.lgButton.isActivated = false
                        val userId = auth.currentUser!!.uid
                        db.collection("users").document(userId)
                            .get()
                            .addOnSuccessListener { documentSnapshot ->
                                val username = documentSnapshot.getString("username")
                                val className = documentSnapshot.getString("class")
                                val loginDn = documentSnapshot.getString("diaryLogin")
                                val passwordDn = documentSnapshot.getString("diaryPassword")
                                val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
                                sharedPref.edit {
                                    putString("login", username)
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


        regButton.setOnClickListener {
            finish()
            val i = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(i)
        }

    }

}

