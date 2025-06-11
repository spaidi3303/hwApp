package com.sammy.hwapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sammy.hwapp.LogIo.LogIo.getMarks
import org.json.JSONArray
import androidx.core.content.edit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sammy.hwapp.LogIo.LogIo.checkAdm
import com.sammy.hwapp.LogIo.LogIo.getAllMarks
import com.sammy.hwapp.LogIo.LogIo.getMembers
import com.sammy.hwapp.LogIo.LogIo.loginUser
import org.json.JSONObject

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
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        if (auth.currentUser != null) {
            val userId = auth.currentUser!!.uid
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val i = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(i)
                    finish()

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

            getMarks(login, passw) { result ->
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

            getAllMarks(login, passw) { avgResult ->
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

            val className = sharedPref.getString("class", "") ?: ""
            val _login = sharedPref.getString("login", "") ?: ""
            checkAdm(className, _login) { result ->
                val status = result?.toIntOrNull()
                sharedPref.edit { putString("ifAdmin", status.toString()) }
            }

            getMembers(className) { result ->
                val res = JSONObject(result!!)
                val memberList = res.getJSONArray("members")
                val adminList = res.getJSONArray("admins")

                val membersJsonString = memberList.toString()
                val adminsJsonString = adminList.toString()

                sharedPref.edit {
                    putString("members", membersJsonString)
                    putString("admins", adminsJsonString)
                    apply()
                }
            }

        } else {
            callback()
        }
    }
}