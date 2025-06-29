package com.sammy.hwapp.splash

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.sammy.hwapp.splash.LogIo.checkAdm
import com.sammy.hwapp.splash.LogIo.getAllMarks
import com.sammy.hwapp.splash.LogIo.getMarks
import com.sammy.hwapp.splash.LogIo.getMembers
import org.json.JSONArray
import org.json.JSONObject

class Load {
}
object DataLoader {
    fun loader(context: Context, callback: () -> Unit = {}) {
        val sharedPref = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        if (sharedPref.contains("login")) {
            val login = sharedPref.getString("loginDn", "") ?: ""
            val passw = sharedPref.getString("passwordDn", "") ?: ""
            Log.d("--------", login.toString())
            Log.d("--------", "---------------------------")
            val marksPref = context.getSharedPreferences("userMarks", Context.MODE_PRIVATE)

            getMarks(login, passw) { result ->
                val dataList = JSONArray(result)
                val jsonArray = JSONArray()

                for (i in 0 until dataList.length()) {
                    val item = dataList.getJSONArray(i)
                    val entry = JSONArray()
                    entry.put(item.getString(0))
                    entry.put(item.getString(1))
                    entry.put(item.getInt(2))
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
                    val marksArray = item.getJSONArray(2)

                    val entry = JSONArray()
                    entry.put(subject)
                    entry.put(avg)
                    entry.put(marksArray)
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