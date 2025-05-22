package com.sammy.hwapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sammy.hwapp.LogIo.LogIo.checkDiaries
import com.sammy.hwapp.LogIo.LogIo.getMarks
import org.json.JSONArray
import androidx.core.content.edit
import com.sammy.hwapp.LogIo.LogIo.getAllMarks
import com.sammy.hwapp.LogIo.LogIo.loginUser
import com.sammy.hwapp.LoginActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        DataLoader.loadMarks(this) {
            startMainActivity()
        }
    }


    private fun startMainActivity() {
        val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)

        if (sharedPref.contains("login")) {
            val login = sharedPref.getString("login", "").toString()
            val password = sharedPref.getString("password", "").toString()

            loginUser(login, password) { result, error ->
                runOnUiThread {
                    val status = result?.toIntOrNull()
                    if (status == 2) {
                        val i = Intent(this@SplashActivity, MainActivity::class.java)
                        startActivity(i)
                    } else {
                        val i = Intent(this@SplashActivity, LoginActivity::class.java)
                        startActivity(i)
                    }
                    finish()
                }
            }
        } else {
            val i = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(i)
            finish()
        }
    }
}

object DataLoader {
    fun loadMarks(context: Context, callback: () -> Unit = {}) {
        val sharedPref = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        if (sharedPref.contains("login")) {
            val login = sharedPref.getString("loginDn", "") ?: ""
            val passw = sharedPref.getString("passwordDn", "") ?: ""

            val marksPref = context.getSharedPreferences("userMarks", Context.MODE_PRIVATE)

            getMarks(login, passw) { result, error ->
                val dataList = JSONArray(result)
                val jsonArray = JSONArray()

                for (i in 0 until dataList.length()) {
                    val item = dataList.getJSONArray(i)
                    val entry = JSONArray()
                    entry.put(item.getString(0)) // дата
                    entry.put(item.getString(1)) // предмет
                    entry.put(item.getInt(2))    // оценка
                    jsonArray.put(entry)
                }

                marksPref.edit {
                    putString("marks", jsonArray.toString())
                }
            }

            getAllMarks(login, passw) { avgResult, avgError ->
                val rawData = JSONArray(avgResult)
                val avgMarksArray = JSONArray()

                for (i in 0 until rawData.length()) {
                    val item = rawData.getJSONArray(i)
                    val subject = item.getString(0)
                    val avg = item.getString(1)
                    val entry = JSONArray()
                    entry.put(subject)
                    entry.put(avg)
                    avgMarksArray.put(entry)
                }

                marksPref.edit {
                    putString("marks_all", avgMarksArray.toString())
                }
                callback()
            }

        } else {
            callback()
        }
    }
}