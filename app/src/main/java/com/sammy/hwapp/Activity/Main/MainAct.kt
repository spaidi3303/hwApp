package com.sammy.hwapp.Activity.Main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import androidx.navigation.NavHostController
import com.sammy.hwapp.Screens.DatePickerModal
import com.sammy.hwapp.navigation.MainScreen
import com.sammy.hwapp.topBar.DrawerBody
import com.sammy.hwapp.topBar.DrawerHeader
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainSActivity(navHostController: NavHostController) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val context = LocalContext.current
    val activity = context as? Activity
    val prefs = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
    // Date Picker state с сегодняшней датой
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    var showDatePicker by remember { mutableStateOf(false) }

    // Текущая дата для отображения
    var selectedDate by remember {
        mutableStateOf(
            datePickerState.selectedDateMillis?.let { convertMillisToDate(it) }
                ?: convertMillisToDate(System.currentTimeMillis())
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerHeader()
                DrawerBody()
            }
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(text = selectedDate) },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Red
                        ),
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Меню")
                            }
                        },
                        actions = {
                            IconButton(onClick = { showDatePicker = !showDatePicker }) {
                                Icon(Icons.Filled.DateRange, contentDescription = "Выбрать дату")
                            }
                            IconButton(onClick = {
                                prefs.edit { clear() }
                                val activity = (context as? Activity)
                                activity?.finishAffinity()

                            }) {
                                Icon(Icons.Filled.ExitToApp, contentDescription = "Выход")
                            }
                        }
                    )
                }
            ) {
                Box(modifier = Modifier.padding(it)) {
                    MainScreen(currentDate = selectedDate)

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
            }
        }
    )
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}
