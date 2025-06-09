package com.sammy.hwapp

import android.app.DatePickerDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.icu.util.Calendar
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.sammy.hwapp.LogIo.LogIo.addHw
import com.sammy.hwapp.LogIo.LogIo.checkAdm
import com.sammy.hwapp.LogIo.LogIo.checkLessonInDay

fun showAddHomeworkDialog(context: Context) {
    val dialogView =
        LayoutInflater.from(context).inflate(R.layout.dialog_add_homework, null)
    val subjectSpinner = dialogView.findViewById<Spinner>(R.id.subjectSpinner)
    val homeworkText = dialogView.findViewById<EditText>(R.id.homeworkText)
    val dateText = dialogView.findViewById<TextView>(R.id.datePickerText)
    val sendBtn = dialogView.findViewById<Button>(R.id.sendHomeworkBtn)
    val subjects = listOf(
        "ОБЖ", "География", "Физкультура", "Литература", "История Церкви",
        "Английский", "ИП", "Химия", "ДРЯ", "Русский язык", "ЦСЯ/Литургика", "Биология",
        "Информатика", "Обществознание", "История", "Физика", "ОПВ", "Алгебра", "Геометрия"
    )

    subjectSpinner.adapter =
        ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, subjects)

    val calendar = Calendar.getInstance()

    dateText.setOnClickListener {
        val datePicker = DatePickerDialog(
            context,
            { _, year, month, day ->
                val selectedDate = "%02d.%02d.%04d".format(day, month + 1, year)
                dateText.text = selectedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    val dialog = android.app.AlertDialog.Builder(context)
        .setView(dialogView)
        .create()
    val sharedPref = context.getSharedPreferences("UserData", MODE_PRIVATE)
    val className = sharedPref.getString("class", "").toString()

    sendBtn.setOnClickListener {
        val selectedSubject = subjectSpinner.selectedItem.toString()
        val text = homeworkText.text.toString()
        val date = dateText.text.toString()

        if (text.isBlank() || date == "Выбрать дату") {
            Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
            return@setOnClickListener
        }

        val parts = date.split(".")
        val day = parts[0].toInt()
        val month = parts[1].toInt() - 1
        val year = parts[2].toInt()

        val cal = java.util.Calendar.getInstance()
        cal.set(year, month, day)
        val weekdayNum =
            cal.get(java.util.Calendar.DAY_OF_WEEK) // 1 = Sunday, 2 = Monday, ..., 7 = Saturday
        val weekday = when (weekdayNum) {
            java.util.Calendar.MONDAY -> "monday"
            java.util.Calendar.TUESDAY -> "tuesday"
            java.util.Calendar.WEDNESDAY -> "wednesday"
            java.util.Calendar.THURSDAY -> "thursday"
            java.util.Calendar.FRIDAY -> "friday"
            java.util.Calendar.SATURDAY -> "saturday"
            java.util.Calendar.SUNDAY -> null
            else -> null
        }
        if (weekday == null) {
            Toast.makeText(
                context,
                "Нельзя задать домашку на воскресенье. Это грех!",
                Toast.LENGTH_SHORT
            ).show()
            return@setOnClickListener
        }
        checkLessonInDay(className, weekday, selectedSubject) { result ->
            val status = result?.toInt()

            if (context is android.app.Activity) {
                context.runOnUiThread {
                    if (status == 0) {
                        Toast.makeText(
                            context,
                            "В этот день нет предмета '$selectedSubject'",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        addHw(
                            text,
                            selectedSubject,
                            dateText.text.toString(),
                            className
                        ) { result ->

                            context.runOnUiThread {
                                if (result?.toInt() == 1) {
                                    Toast.makeText(
                                        context,
                                        "Домашнее задание было успешно добавлено!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    if (dialog.isShowing) dialog.dismiss()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Произошла ошибка",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                        }
                    }
                }
            }

        }
    }


    dialog.show()
}
