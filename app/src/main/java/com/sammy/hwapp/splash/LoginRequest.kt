package com.sammy.hwapp.splash

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.io.IOException

object LogIo {
    fun loginUser(login: String, password: String, callback: (result: String?) -> Unit) {
        val json = JSONObject()
        json.put("username", login)
        json.put("password", password)
        response("user_exists", json, callback)
    }

    fun registerUser(login: String, password: String, className: String, loginDn: String, passwordDn: String, callback: (result: String?) -> Unit){
        val json = JSONObject()
        json.put("login", login)
        json.put("password", password)
        json.put("class_", className)
        json.put("login_dnevnik", loginDn)
        json.put("password_dnevnik", passwordDn)
        response("reg_user", json, callback)
    }

    fun checkDiaries(login: String, password: String, callback: (result: String?) -> Unit) {
        val json = JSONObject()
        json.put("login", login)
        json.put("password", password)
        response("get_marks/check", json, callback)
    }

    fun getDatas(login: String, callback: (result: String?) -> Unit) {
        val json = JSONObject()
        json.put("login", login)
        response("user_exists/get_datas", json, callback)
    }

    fun getMarks(login: String, password: String, callback: (result: String?) -> Unit) {
        val json = JSONObject()
        json.put("login", login)
        json.put("password", password)
        response("get_marks", json, callback)
    }

    fun getAllMarks(login: String, password: String, callback: (result: String?) -> Unit) {
        val json = JSONObject()
        json.put("login", login)
        json.put("password", password)
        response("get_marks/all", json, callback)
    }

    fun checkAdm(className: String, login: String, callback: (result: String?) -> Unit) {
        val json = JSONObject()
        json.put("class_name", className)
        json.put("login", login)
        response("database/check_admin", json, callback)
    }

    fun checkLessonInDay(className: String, weekday: String, subject: String, callback: (result: String?) -> Unit){
        val json = JSONObject()
        json.put("class_name", className)
        json.put("subject", subject)
        json.put("weekday", weekday)
        response("database/less_in_day", json, callback)
    }

    fun addHw(hw: String, subject: String, date: String, className: String, callback: (result: String?) -> Unit){
        val json = JSONObject()
        json.put("hw", hw)
        json.put("subject", subject)
        json.put("date", date)
        json.put("class_name", className)
        response("database/add_hw", json, callback)
    }

    fun getHw(date: String, className: String, callback: (result: String?) -> Unit){
        val json = JSONObject()
        json.put("date", date)
        json.put("class_name", className)
        response("database/get_hw", json, callback)
    }

    fun getMembers(className: String, callback: (String?) -> Unit) {
        val json = JSONObject()
        json.put("class_name", className)
        response("database/get_members", json, callback)
    }

    private fun response(method: String, json: JSONObject, callback: (result: String?) -> Unit) {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = json.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("http://95.105.83.21:8000/$method/")
            .post(body)
            .build()

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("HTTP", "Ошибка запроса", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful) {
                    callback(responseBody)
                } else {
                    Log.w("HTTP", "Ошибка ${response.code}: $responseBody")
                }
            }
        })
    }

}
