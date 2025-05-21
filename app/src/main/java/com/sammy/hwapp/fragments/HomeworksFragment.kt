package com.sammy.hwapp.fragments

import android.app.DatePickerDialog
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sammy.hwapp.LogIo.LogIo.checkAdm
import com.sammy.hwapp.LogIo.LogIo.getHw
import com.sammy.hwapp.R
import com.sammy.hwapp.showAddHomeworkDialog
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

data class HwEntry(
    val subject: String,
    val homework: String
)

class HwAdapter(private val homework: List<HwEntry>) :
    RecyclerView.Adapter<HwAdapter.HwViewHolder>() {

    class HwViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val hwSubject: TextView = view.findViewById(R.id.hwSubject)
        val hwText: TextView = view.findViewById(R.id.hwText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HwViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hw, parent, false)
        return HwViewHolder(view)
    }

    override fun onBindViewHolder(holder: HwViewHolder, position: Int) {
        val item = homework[position]
        holder.hwSubject.text = item.subject
        holder.hwText.text = item.homework
    }

    override fun getItemCount(): Int = homework.size
}

class HomeworksFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HwAdapter

    private lateinit var calendarIcon: ImageView
    private lateinit var selectedDateText: TextView
    private lateinit var laterDay: ImageView
    private lateinit var nextDay: ImageView
    private lateinit var plusHw: ImageView

    private val calendar: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_homeworks, container, false)

        selectedDateText = view.findViewById(R.id.centerDateText)
        laterDay = view.findViewById(R.id.leftDay)
        nextDay = view.findViewById(R.id.rightDay)
        plusHw = view.findViewById(R.id.plusHw)
        recyclerView = view.findViewById(R.id.hwRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(divider)

        val sharedPref = requireContext().getSharedPreferences("UserData", MODE_PRIVATE)
        val login = sharedPref.getString("login", "") ?: ""
        val className = sharedPref.getString("class", "") ?: ""

        checkAdm(className, login) { result, error ->
            requireActivity().runOnUiThread {
                val status = result?.toIntOrNull()
                if (status == 1) {
                    plusHw.visibility = View.VISIBLE
                }
            }
        }
        recyclerView = view.findViewById(R.id.hwRecyclerView)
        updateDateText(className)

        plusHw.setOnClickListener {
            showAddHomeworkDialog(requireContext())
        }

        selectedDateText.setOnClickListener {
            showDatePicker(className)
        }

        laterDay.setOnClickListener {
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            updateDateText(className)
        }

        nextDay.setOnClickListener {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            updateDateText(className)
        }

        return view
    }

    private fun updateDateText(className: String) {
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        val date = "%02d.%02d.%04d".format(day, month, year)
        val formatter = java.text.SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val parsedDate = formatter.parse(date)

        val calendar = Calendar.getInstance()
        calendar.time = parsedDate!!

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        if (dayOfWeek == Calendar.SUNDAY) {
            Toast.makeText(requireContext(), "В воскресенье домашки нет", Toast.LENGTH_SHORT).show()
            return
        }
        selectedDateText.text = date
        showHw(date, className)
    }

    private fun showHw(date: String, className: String) {
        getHw(date, className) { result, error ->
            requireActivity().runOnUiThread {
                if (error != null || result == null) return@runOnUiThread

                try {
                    val hwList = mutableListOf<HwEntry>()
                    val jsonObject = JSONObject(result)

                    val keys = jsonObject.keys()
                    while (keys.hasNext()) {
                        val subject = keys.next()
                        val homeworksArray = jsonObject.getJSONArray(subject)

                        for (i in 0 until homeworksArray.length()) {
                            val homework = homeworksArray.getString(i)
                            hwList.add(HwEntry(subject, homework))
                        }
                    }

                    adapter = HwAdapter(hwList)
                    recyclerView.adapter = adapter

                } catch (e: Exception) {
                    e.printStackTrace()
                    // Можешь показать Toast, лог или сообщение об ошибке
                }
            }
        }
    }


    private fun showDatePicker(className: String) {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _: DatePicker, y: Int, m: Int, d: Int ->
                calendar.set(Calendar.YEAR, y)
                calendar.set(Calendar.MONTH, m)
                calendar.set(Calendar.DAY_OF_MONTH, d)
                updateDateText(className)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

}
