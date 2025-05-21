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

data class AvgMarkEntry(val subject: String, val avg: String)

class AvgMarksAdapter(private val marks: List<AvgMarkEntry>) :
    RecyclerView.Adapter<AvgMarksAdapter.MarkViewHolder>() {

    class MarkViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val subjectText: TextView = view.findViewById(R.id.subjectText)
        val avgMarkText: TextView = view.findViewById(R.id.avgMarkText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_avg_grades, parent, false)
        return MarkViewHolder(view)
    }

    override fun onBindViewHolder(holder: MarkViewHolder, position: Int) {
        val item = marks[position]
        holder.subjectText.text = item.subject
        holder.avgMarkText.text = item.avg
    }

    override fun getItemCount(): Int = marks.size
}



class AllGradesFragment : Fragment(R.layout.fragment_all_marks) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AvgMarksAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.avgMarksRecyclerView)

        val prefs = requireContext().getSharedPreferences("userMarks", Context.MODE_PRIVATE)
        val jsonString = prefs.getString("marks_all", "[]")
        val jsonArray = JSONArray(jsonString)

        val marksList = mutableListOf<AvgMarkEntry>()
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONArray(i)
            val subject = item.getString(0)
            val avg = item.getString(1)
            marksList.add(AvgMarkEntry(subject, avg))
        }

        adapter = AvgMarksAdapter(marksList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        val dividerItemDecoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(dividerItemDecoration)
    }
}

