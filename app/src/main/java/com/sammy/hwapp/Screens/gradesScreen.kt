package com.sammy.hwapp.Screens

import android.content.Context
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray

data class GradeEntry(
    val date: String,
    val subject: String,
    val grade: String
)

@Composable
fun GradesScreen() {
    val activity = LocalContext.current as? android.app.Activity
    val sharedPref = activity?.getSharedPreferences("userMarks", Context.MODE_PRIVATE)
    val marksJson = sharedPref?.getString("marks", "[]")
    val jsonArray = JSONArray(marksJson)

    val gradeEntries = remember { mutableStateListOf<GradeEntry>() }

    for (i in 0 until jsonArray.length()) {
        val item = jsonArray.getJSONArray(i)
        val date = item.getString(0)
        val subject = item.getString(1)
        val grade = item.getString(2)
        gradeEntries.add(GradeEntry(date, subject, grade))
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(gradeEntries) { entry ->
            GradeCard(entry)
        }
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GradeCard(entry: GradeEntry) {
    val color = when (entry.grade.toFloatOrNull() ?: 0f) {
        in 0f..<3f -> Color.Red
        in 3f..<4f -> Color(0xFFFFA000)
        in 4f..5f -> Color(0xFF4CAF50)
        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.subject,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = entry.date,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Text(
                    text = entry.grade,
                    fontSize = 20.sp,
                    color = color,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
