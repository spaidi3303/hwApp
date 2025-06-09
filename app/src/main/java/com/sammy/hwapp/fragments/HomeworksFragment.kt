package com.sammy.hwapp.fragments

import android.app.DatePickerDialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sammy.hwapp.LogIo.LogIo.checkAdm
import com.sammy.hwapp.LogIo.LogIo.getHw
import com.sammy.hwapp.LoginActivity
import com.sammy.hwapp.R
import com.sammy.hwapp.showAddHomeworkDialog
import org.json.JSONArray
import java.util.*
import androidx.core.content.edit
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sammy.hwapp.databinding.FragmentHomeworksBinding
import androidx.core.view.isGone

data class HwEntry(
    val subject: String,
    val time: String,
    val homework: String
)


class HwAdapter(private val homework: List<HwEntry>) :
    RecyclerView.Adapter<HwAdapter.HwViewHolder>() {

    class HwViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val hwSubject: TextView = view.findViewById(R.id.hwSubject)
        val hwTime: TextView = view.findViewById(R.id.hwTime)
        val hwHomework: TextView = view.findViewById(R.id.hwHomework)
        init {
            view.setOnClickListener {
                if (hwHomework.isGone) {
                    hwHomework.visibility = View.VISIBLE
                } else {
                    hwHomework.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HwViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hw, parent, false)
        return HwViewHolder(view)
    }

    override fun onBindViewHolder(holder: HwViewHolder, position: Int) {
        val item = homework[position]
        holder.hwSubject.text = item.subject
        holder.hwTime.text = item.time
        holder.hwHomework.text = item.homework
    }

    override fun getItemCount(): Int = homework.size
}

class HomeworksFragment : Fragment() {
    private lateinit var binding: FragmentHomeworksBinding
    private lateinit var adapter: HwAdapter
    private val calendar: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeworksBinding.inflate(inflater, container, false)

        binding.hwRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val divider =
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.hwRecyclerView.addItemDecoration(divider)

        val sharedPref = requireContext().getSharedPreferences("UserData", MODE_PRIVATE)
        val login = sharedPref.getString("login", "") ?: ""
        val className = sharedPref.getString("class", "") ?: ""

        checkAdm(className, login) { result ->
            if (!isAdded || view == null || activity == null || activity?.isFinishing == true) return@checkAdm
            requireActivity().runOnUiThread {
                val status = result?.toIntOrNull()
                if (status == 1) {
                    binding.plusHw.visibility = View.VISIBLE
                }
            }
        }
        updateDateText(className)

        binding.plusHw.setOnClickListener {
            showAddHomeworkDialog(requireContext())
        }

        binding.centerDateText.setOnClickListener {
            showDatePicker(className)
        }

        binding.leftDay.setOnClickListener {
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            updateDateText(className)
        }

        binding.rightDay.setOnClickListener {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            updateDateText(className)
        }

        binding.exit.setOnClickListener {
            logout(sharedPref)
        }

        return binding.root
    }

    private fun updateDateText(className: String) {
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        val date = "%02d.%02d.%04d".format(day, month, year)

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        if (dayOfWeek == Calendar.SUNDAY) {
            val hwList = listOf(
                HwEntry("Воскресенье", "0_0", "Храни вас Господь!")
            )
            binding.centerDateText.text = date
            binding.hwRecyclerView.adapter = HwAdapter(hwList)

            return
        }
        binding.centerDateText.text = date
        showHw(date, className)
    }


    private fun showHw(date: String, className: String) {
        val lessonTimes = arrayOf(
            "08:00\n08:40",
            "08:50\n09:30",
            "09:40\n10:20",
            "10:30\n11:10",
            "11:20\n12:00",
            "12:10\n12:50",
            "13:00\n13:40",
            "13:50\n14:30"
        )


        getHw(date, className) { result ->

            Thread {
                val hwList = mutableListOf<HwEntry>()

                val jsonArray = JSONArray(result!!)
                for (i in 0 until jsonArray.length()) {
                    val entry = jsonArray.getString(i)
                    val parts = entry.split(".", limit = 2)
                    if (parts.size == 2) {
                        val subject = "" + i.plus(1) + ". " + parts[0]
                        val homework = parts[1]
                        val list = homework
                            .replace("[", "")
                            .replace("]", "")
                            .replace("'", "")
                            .split(", ")
                        val hw = if (list.size > 1) {
                            list.joinToString("\n")
                        } else {
                            list[0]
                        }

                        hwList.add(HwEntry(subject, lessonTimes[i], hw))
                    }
                }


                activity?.runOnUiThread {
                    if (!isAdded || view == null) return@runOnUiThread
                    adapter = HwAdapter(hwList)
                    binding.hwRecyclerView.adapter = adapter
                }
            }.start()
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

    private fun logout(sharedPref: SharedPreferences){
        requireActivity().finish()
        val i = Intent(requireContext(), LoginActivity::class.java)
        startActivity(i)
        sharedPref.edit{ clear().apply() }


    }

}
