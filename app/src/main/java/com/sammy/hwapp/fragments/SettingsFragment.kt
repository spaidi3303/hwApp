package com.sammy.hwapp.fragments

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sammy.hwapp.databinding.FragmentSettingsBinding


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
        "$className класс".also { binding.classText.text = it }
        if (ifAdmin == "1") binding.adminsLine.visibility = View.VISIBLE








        return binding.root

    }
}
