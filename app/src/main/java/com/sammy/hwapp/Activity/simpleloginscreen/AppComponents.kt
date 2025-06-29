package com.montanainc.simpleloginscreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.montanainc.simpleloginscreen.ui.theme.AccentColor
import com.montanainc.simpleloginscreen.ui.theme.BgColor
import com.montanainc.simpleloginscreen.ui.theme.GrayColor
import com.montanainc.simpleloginscreen.ui.theme.Primary
import com.montanainc.simpleloginscreen.ui.theme.Secondary
import com.montanainc.simpleloginscreen.ui.theme.TextColor

@Composable
fun NormalTextComponent(value: String) {
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp),
        style = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal
        ),
        color = TextColor,
        textAlign = TextAlign.Center
    )
}

@Composable
fun HeadingTextComponent(value: String) {
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(),
        style = TextStyle(
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Normal
        ),
        color = TextColor,
        textAlign = TextAlign.Center
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTextFieldComponent(
    labelValue: String,
    icon: ImageVector,
    textValue: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = textValue,
        onValueChange = onValueChange,
        label = { Text(labelValue) },
        leadingIcon = { Icon(imageVector = icon, contentDescription = null) },
        isError = isError,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = if (isError) Color.Red else AccentColor,
            unfocusedIndicatorColor = if (isError) Color.Red else GrayColor,
            focusedLabelColor = if (isError) Color.Red else AccentColor,
            cursorColor = Primary,
            focusedLeadingIconColor = if (isError) Color.Red else AccentColor,
            unfocusedLeadingIconColor = if (isError) Color.Red else GrayColor,
            disabledIndicatorColor = Color.Transparent,
            unfocusedContainerColor = BgColor,
            focusedContainerColor = BgColor,
            errorContainerColor = BgColor,
            disabledContainerColor = BgColor,
            focusedTextColor = TextColor,
            unfocusedTextColor = TextColor
        ),
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        keyboardOptions = KeyboardOptions.Default
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordTextFieldComponent(
    labelValue: String,
    icon: ImageVector,
    textValue: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = textValue,
        onValueChange = onValueChange,
        label = { Text(text = labelValue) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        },
        trailingIcon = {
            val iconImage =
                if (isPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
            val description = if (isPasswordVisible) "Hide password" else "Show password"
            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                Icon(imageVector = iconImage, contentDescription = description)
            }
        },
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        isError = isError,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = if (isError) Color.Red else AccentColor,
            unfocusedIndicatorColor = if (isError) Color.Red else GrayColor,
            focusedLabelColor = if (isError) Color.Red else AccentColor,
            unfocusedLabelColor = if (isError) Color.Red else GrayColor,
            cursorColor = Primary,
            focusedLeadingIconColor = if (isError) Color.Red else AccentColor,
            unfocusedLeadingIconColor = if (isError) Color.Red else GrayColor,
            focusedTextColor = TextColor,
            unfocusedTextColor = TextColor,
            focusedContainerColor = BgColor,
            unfocusedContainerColor = BgColor,
            disabledContainerColor = BgColor
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassSelectorComponent(
    labelValue: String = "Выберите класс",
    icon: ImageVector? = null,
    classList: List<String> = listOf("11А", "11Б", "11В"),
    selectedClass: String,
    onClassSelected: (String) -> Unit,
    isError: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
    ) {
        OutlinedTextField(
            value = selectedClass,
            onValueChange = { },
            readOnly = true,
            label = { Text(labelValue) },
            leadingIcon = {
                if (icon != null) {
                    Icon(imageVector = icon, contentDescription = null)
                }
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            isError = isError,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = if (isError) Color.Red else AccentColor,
                unfocusedIndicatorColor = if (isError) Color.Red else GrayColor,
                focusedLabelColor = if (isError) Color.Red else AccentColor,
                cursorColor = Primary,
                focusedLeadingIconColor = if (isError) Color.Red else AccentColor,
                unfocusedLeadingIconColor = if (isError) Color.Red else GrayColor,
                disabledIndicatorColor = Color.Transparent,
                unfocusedContainerColor = BgColor,
                focusedContainerColor = BgColor,
                errorContainerColor = BgColor,
                disabledContainerColor = BgColor,
                focusedTextColor = TextColor,
                unfocusedTextColor = TextColor
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            classList.forEach { className ->
                DropdownMenuItem(
                    text = { Text(className) },
                    onClick = {
                        onClassSelected(className)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun RegisterButton(
    onClick: () -> Unit,
    isEnabled: Boolean,
    errorMessage: String? = null,
    navHostController: NavHostController,
) {
    Column(modifier = Modifier
        .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        if (isEnabled) listOf(Secondary, AccentColor)
                        else listOf(Color.LightGray, Color.Gray)
                    ),
                    shape = RoundedCornerShape(50.dp)
                )
                .fillMaxWidth()
                .heightIn(48.dp)
                .then(
                    if (isEnabled) Modifier.clickable { onClick() } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Зарегистрироваться",
                color = Color.White,
                fontSize = 20.sp
            )
        }

        if (!errorMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage, color = Color.Red, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(32.dp))

        Row {
            Text(
                text = "Уже есть аккаунт? ",
                style = TextStyle(color = TextColor, fontSize = 15.sp)
            )
            Text(
                text = "войти",
                style = TextStyle(color = Secondary, fontSize = 15.sp),
                modifier = Modifier.clickable {
                    navHostController.navigate("Login")
                }
            )
        }
    }
}
