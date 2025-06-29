package com.sammy.hwapp.Screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHomeworkDialog(
    onDismiss: () -> Unit,
    onSubmit: (subject: String, homework: String, date: String) -> Unit,
    selectedDate: String,
    onDateClick: () -> Unit
){
    val context = LocalContext.current
    val subjects = listOf(
        "ОБЖ", "География", "Физкультура", "Литература", "История Церкви",
        "Английский", "ИП", "Химия", "ДРЯ", "Русский язык", "ЦСЯ/Литургика", "Биология",
        "Информатика", "Обществознание", "История", "Физика", "ОПВ", "Алгебра", "Геометрия"
    )

    var selectedSubject by remember { mutableStateOf(subjects.first()) }
    var expanded by remember { mutableStateOf(false) }
    var homeworkText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                if (homeworkText.isBlank() || selectedDate.isBlank()) {
                    Toast.makeText(
                        context,
                        "Заполните все поля",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    onSubmit(selectedSubject, homeworkText, selectedDate)
                    onDismiss()
                }
            }) {
                Text("Отправить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        },
        title = { Text("Добавить домашку") },
        text = {
            Column {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedSubject,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Предмет") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .clickable { expanded = !expanded }
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        subjects.forEach { subject ->
                            DropdownMenuItem(
                                text = { Text(subject) },
                                onClick = {
                                    selectedSubject = subject
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.padding(8.dp))

                OutlinedTextField(
                    value = homeworkText,
                    onValueChange = { homeworkText = it },
                    label = { Text("Домашнее задание") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.padding(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            println("Дата нажата (Box)")
                            onDateClick()
                        }
                ) {
                    OutlinedTextField(
                        value = if (selectedDate.isNotBlank()) selectedDate else "Выбрать дату",
                        onValueChange = {},
                        label = { Text("Дата") },
                        readOnly = true,
                        enabled = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
