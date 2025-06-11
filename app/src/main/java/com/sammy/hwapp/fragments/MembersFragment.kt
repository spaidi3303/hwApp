package com.sammy.hwapp.fragments

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sammy.hwapp.R
import com.sammy.hwapp.databinding.FragmentMembersBinding
import org.json.JSONArray

data class MemberEntry(
    val name: String
)

class MemberAdapter(private val members: List<MemberEntry>) :
    RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {

    class MemberViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val memberName: TextView = view.findViewById(R.id.personName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu_settings, parent, false)
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val item = members[position]
        holder.memberName.text = item.name
    }

    override fun getItemCount(): Int = members.size
}



class MembersFragment : Fragment() {
    private lateinit var binding: FragmentMembersBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MemberAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMembersBinding.inflate(layoutInflater)
        val titleMenuInActivity = requireActivity().findViewById<TextView>(R.id.TitleMenu)
        titleMenuInActivity?.text = "Ученики"

        recyclerView = binding.recyclerView
        val memberList = mutableListOf<MemberEntry>()
        val sharedPref = requireContext().getSharedPreferences("UserData", MODE_PRIVATE)
        val members = JSONArray(sharedPref.getString("members", "")?: "")
        for (i in 0 until members.length()){
            memberList.add(MemberEntry(members[i].toString()))
        }
        adapter = MemberAdapter(memberList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(dividerItemDecoration)

        return binding.root
    }

}

class AdminsFragment : Fragment() {
    private lateinit var binding: FragmentMembersBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MemberAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMembersBinding.inflate(layoutInflater)
        val titleMenuInActivity = requireActivity().findViewById<TextView>(R.id.TitleMenu)
        titleMenuInActivity?.text = "Администраторы"

        recyclerView = binding.recyclerView
        val memberList = mutableListOf<MemberEntry>()
        val sharedPref = requireContext().getSharedPreferences("UserData", MODE_PRIVATE)
        val members = JSONArray(sharedPref.getString("admins", "")?: "")
        for (i in 0 until members.length()){
            memberList.add(MemberEntry(members[i].toString()))
        }
        adapter = MemberAdapter(memberList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(dividerItemDecoration)

        return binding.root
    }

}