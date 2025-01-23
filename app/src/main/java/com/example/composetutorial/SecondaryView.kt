package com.example.composetutorial

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecondaryView(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    Button(
                        onClick = {
                            navController.navigate(View.MainView.route) {
                                popUpTo(View.MainView.route) { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .height(42.dp)
                            .padding(start = 10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black
                        )
                    ) {
                        Text("Back")
                    }
                },
                title = { Row {} }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            MessageCard(Message(author = "Lexi", body = "Hello, this is the second view"), 1)
        }
    }

}