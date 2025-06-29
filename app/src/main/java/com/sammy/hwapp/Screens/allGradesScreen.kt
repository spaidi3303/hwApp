package com.sammy.hwapp.Screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import org.json.JSONArray

@Composable
fun AllGradesScreen() {
    val activity = LocalContext.current as? android.app.Activity
    val prefs = activity?.getSharedPreferences("userMarks", Context.MODE_PRIVATE)
    val jsonString = prefs?.getString("marks_all", "[]")
    val jsonArray = JSONArray(jsonString)

    val subjectList = remember { mutableStateListOf<String>() }
    val gradesList = remember { mutableStateListOf<String>() }
    val marksList = remember { mutableStateListOf<List<String>>() }

    for (i in 0 until jsonArray.length()) {
        val innerArray = jsonArray.getJSONArray(i)
        val subject = innerArray.getString(0)
        val avg = innerArray.getString(1)
        val grades = innerArray.getJSONArray(2)

        val gradeStrings = List(grades.length()) { j ->
            grades.getString(j)
        }

        subjectList.add(subject)
        gradesList.add(avg)
        marksList.add(gradeStrings)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(
            top = 16.dp,
            bottom = 100.dp
        )
    ) {
        itemsIndexed(subjectList) { index, item ->
            SubjectAllGradesCard(item, gradesList[index], marksList[index])
        }
    }


}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SubjectAllGradesCard(
    subject: String,
    grades: String,
    allGrades: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    val color = when {
        (grades.toFloatOrNull() ?: 0f) < 3f -> Color.Red
        (grades.toFloatOrNull() ?: 0f) < 4f -> Color(0xFFFFA000)
        (grades.toFloatOrNull() ?: 0f) <= 5f -> Color(0xFF4CAF50)
        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }, // ← кликабельность
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
                    text = grades,
                    fontSize = 16.sp,
                    color = color
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = allGrades.joinToString(", "),
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}
