package com.sammy.hwapp.topBar

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sammy.hwapp.R

@Composable
fun DrawerHeader() {
    Column(modifier = Modifier.fillMaxWidth()
        .height(210.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.varnic_fon),
            contentDescription = "Drawer Header Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}


@Composable
fun DrawerBody() {
    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(10.dp),
    ) {
        items(5) {
            Text(text = "Menu $it",
                modifier = Modifier.padding(vertical = 8.dp).clickable{
                }
            )
        }
    }
}