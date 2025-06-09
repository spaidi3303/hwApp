package com.sammy.hwapp

import com.sammy.hwapp.fragments.HomeworksFragment
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sammy.hwapp.databinding.ActivityMainBinding
import com.sammy.hwapp.fragments.AllGradesFragment
import com.sammy.hwapp.fragments.MarksFragment
import com.sammy.hwapp.fragments.SettingsFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.selectedItemId = R.id.nav_grades

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, MarksFragment())
                .commit()
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_homeworks -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_container, HomeworksFragment())
                        .commit()
                    true
                }
                R.id.nav_grades -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_container, MarksFragment())
                        .commit()
                    true
                }
                R.id.nav_all_grades -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_container, AllGradesFragment())
                        .commit()
                    true
                }
                R.id.settings -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_container, SettingsFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }
    }
}