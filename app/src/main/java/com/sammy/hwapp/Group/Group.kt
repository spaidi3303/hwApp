package com.sammy.hwapp.Group

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import com.sammy.hwapp.DataLoader
import com.sammy.hwapp.LogIo.LogIo.checkDiaries
import com.sammy.hwapp.LogIo.LogIo.registerUser
import com.sammy.hwapp.MainActivity
import com.sammy.hwapp.R
import com.sammy.hwapp.RegisterActivity
import com.sammy.hwapp.databinding.ActivityRegisterBinding
import java.util.Date

data class Group(
    var id: String = "",
    var name: String = "",
    var description: String = "",
    var members: MutableList<String> = mutableListOf(),
    @ServerTimestamp
    var createdAt: Date? = null
) {
    constructor() : this("", "", "", mutableListOf(), null)

    @Exclude
    fun getGroupId(): String {
        return id
    }
}

