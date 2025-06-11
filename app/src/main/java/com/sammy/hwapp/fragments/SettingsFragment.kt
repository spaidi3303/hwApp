package com.sammy.hwapp.fragments

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sammy.hwapp.LoginActivity
import com.sammy.hwapp.SettingActivity
import com.sammy.hwapp.SplashActivity
import com.sammy.hwapp.databinding.FragmentSettingsBinding
import org.json.JSONArray


class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        val sharedPref = requireContext().getSharedPreferences("UserData", MODE_PRIVATE)
        val className = sharedPref.getString("class", "") ?: ""
        val ifAdmin = sharedPref.getString("ifAdmin", "") ?: ""
        val members = JSONArray(sharedPref.getString("members", "")?: "")
        val admins = JSONArray(sharedPref.getString("admins", "") ?: "")

        "$className класс".also { binding.classText.text = it }
        binding.memberCount.text = members.length().toString()
        binding.adminCount.text = admins.length().toString()
        if (ifAdmin == "1") binding.adminsLine.visibility = View.VISIBLE

        binding.membersLine.setOnClickListener {
            val intent = Intent(requireActivity(), SettingActivity::class.java)
            intent.putExtra("fragment", "members")
            startActivity(intent)
        }
        binding.adminsLine.setOnClickListener {
            val intent = Intent(requireActivity(), SettingActivity::class.java)
            intent.putExtra("fragment", "admins")
            startActivity(intent)
        }







        return binding.root

    }
}
