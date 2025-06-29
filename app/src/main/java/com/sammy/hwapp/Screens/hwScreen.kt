package com.sammy.hwapp.Screens

import android.content.Context.MODE_PRIVATE
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sammy.hwapp.splash.LogIo.addHw
import com.sammy.hwapp.splash.LogIo.getHw
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HwScreen(currentDate: String) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    val sharedPref = context.getSharedPreferences("UserData", MODE_PRIVATE)
    val className = sharedPref.getString("class", "") ?: ""
    val ifAdmin = sharedPref.getString("ifAdmin", "") ?: ""
    val subjectList = remember { mutableStateListOf<String>() }
    val homeworkList = remember { mutableStateListOf<String>() }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }
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
    LaunchedEffect(currentDate) {
        getHw(currentDate, className) { result ->
            val jsonArray = JSONArray(result ?: "[]")
            subjectList.clear()
            homeworkList.clear()

            for (i in 0 until jsonArray.length()) {
                val entry = jsonArray.getString(i)
                val parts = entry.split(".", limit = 2)
                if (parts.size == 2) {
                    subjectList.add(parts[0])
                    val rawHomework = parts[1]
                    val cleanedList = rawHomework
                        .replace("[", "")
                        .replace("]", "")
                        .replace("'", "")
                        .split(", ")
                        .filter { it.isNotBlank() }

                    val homeworkText = if (cleanedList.isNotEmpty()) {
                        cleanedList.joinToString("\n")
                    } else {
                        "Нет домашнего задания"
                    }
                    homeworkList.add(homeworkText)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            itemsIndexed(subjectList) { index, item ->
                SubjectCard(item, lessonTimes[index], homeworkList[index])
            }
        }
        if(ifAdmin == "1"){
            FloatingActionButton(
                onClick = { showDialog = true  },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 150.dp).padding(end = 20.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }
    }
    if (showDialog) {
        AddHomeworkDialog(
            onDismiss = { showDialog = false },
            onSubmit = { subject, homework, selectedDate ->
                showDialog = false
                addHw(
                    homework,
                    subject,
                    selectedDate,
                    className
                ) { result ->
                    val activity = context as? android.app.Activity
                    activity?.runOnUiThread {
                        if (result?.toInt() == 1) {
                            Toast.makeText(
                                context,
                                "Домашнее задание было успешно добавлено!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Произошла ошибка",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                }
            },
            selectedDate = selectedDate,
            onDateClick = { showDatePicker = true }
        )
    }
    if (showDatePicker) {
        DatePickerModal(
            onDateSelected = { millis ->
                millis?.let {
                    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                    selectedDate = sdf.format(Date(it))
                }
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }


}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SubjectCard(
    subject: String,
    time: String,
    homework: String
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = subject,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = time,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Text(
                    text = homework,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}
