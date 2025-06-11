package com.sammy.hwapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sammy.hwapp.databinding.ActivitySettingBinding
import com.sammy.hwapp.fragments.AdminsFragment
import com.sammy.hwapp.fragments.AllGradesFragment
import com.sammy.hwapp.fragments.MembersFragment

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val fragmentName = intent.getStringExtra("fragment")
        if (fragmentName.equals("members")){
            supportFragmentManager.beginTransaction()
                .replace(R.id.setting_fragment_container, MembersFragment())
                .commit()
            true
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.setting_fragment_container, AdminsFragment())
                .commit()
            true
        }
        binding.topMenuBack.setOnClickListener {
            finish()
        }
    }
}