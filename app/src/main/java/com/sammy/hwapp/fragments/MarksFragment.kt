package com.sammy.hwapp.fragments

import android.content.Context

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sammy.hwapp.R
import org.json.JSONArray

data class MarkEntry(
    val date: String,
    val subject: String,
    val mark: Int
)

class MarksAdapter(private val marks: List<MarkEntry>) :
    RecyclerView.Adapter<MarksAdapter.MarkViewHolder>() {

    class MarkViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val subjectText: TextView = view.findViewById(R.id.subjectText)
        val dateText: TextView = view.findViewById(R.id.dateText)
        val markText: TextView = view.findViewById(R.id.markText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_marks, parent, false)
        return MarkViewHolder(view)
    }

    override fun onBindViewHolder(holder: MarkViewHolder, position: Int) {
        val item = marks[position]
        holder.subjectText.text = item.subject
        holder.dateText.text = item.date
        holder.markText.text = item.mark.toString()
    }

    override fun getItemCount(): Int = marks.size
}


class MarksFragment : Fragment(R.layout.fragment_marks) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MarksAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.marksRecyclerView)

        val sharedPref = requireContext().getSharedPreferences("userMarks", Context.MODE_PRIVATE)
        val marksJson = sharedPref.getString("marks", "[]")
        val jsonArray = JSONArray(marksJson)
        val marksList = mutableListOf<MarkEntry>()
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONArray(i)
            val date = item.getString(0).split(".").take(2).joinToString(".")
            val subject = item.getString(1)
            val mark = item.getInt(2)
            marksList.add(MarkEntry(date, subject, mark))
        }


        adapter = MarksAdapter(marksList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(dividerItemDecoration)
    }
}

