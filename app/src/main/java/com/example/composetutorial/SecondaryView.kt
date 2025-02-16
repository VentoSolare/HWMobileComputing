package com.example.composetutorial

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import java.io.File
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecondaryView(navController: NavHostController) {
    val context = LocalContext.current
    var username by remember { mutableStateOf(loadUsername(context)) }
    var imageUri by remember { mutableStateOf(loadImageUri(context)) }
    var notificationsAllowed by remember { mutableStateOf(loadNotificationPreference(context)) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startTemperatureMonitorService(context)
            notificationsAllowed = true
            saveNotificationPreference(context, true)
        } else {
            notificationsAllowed = false
            saveNotificationPreference(context, false)
        }
    }


    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val savedUri = storageImage(context, it)
            if (savedUri != null) {
                imageUri = savedUri
                saveImageUri(context, savedUri)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Button(
                        onClick = {
                            navController.navigate(View.MainView.route) {
                                popUpTo(View.MainView.route) { inclusive = true }
                            }
                        },
                        modifier = Modifier.height(42.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text("Back")
                    }
                },
                navigationIcon = {}
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "User:",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            imageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .clickable { imagePicker.launch("image/*") }
                )
            } ?: Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .clickable { imagePicker.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                Text("Tap to pick image", color = Color.White, textAlign = TextAlign.Center)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Enter username", style = MaterialTheme.typography.titleMedium) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (username.isBlank()) {
                            username = "Anonymous user"
                            saveUsername(context, "Anonymous user")
                        } else {
                            saveUsername(context, username)
                        }
                    }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Enable Notifications")
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = notificationsAllowed,
                    onCheckedChange = { isChecked ->
                        notificationsAllowed = isChecked
                        saveNotificationPreference(context, isChecked)
                        if (isChecked) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            context.stopService(
                                Intent(
                                    context,
                                    TemperatureMonitorService::class.java
                                )
                            )
                        }
                    },
                    colors = SwitchDefaults.colors(checkedTrackColor = Color.Black, uncheckedTrackColor = Color.Black)
                )
            }
        }
    }
}

fun startTemperatureMonitorService(context: Context) {
    ContextCompat.startForegroundService(
        context,
        Intent(context, TemperatureMonitorService::class.java)
    )
}

fun loadNotificationPreference(context: Context): Boolean {
    val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    return sharedPref.getBoolean("notifications_allowed", false)
}

fun saveNotificationPreference(context: Context, allowed: Boolean) {
    val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    sharedPref.edit().putBoolean("notifications_allowed", allowed).apply()
}

private fun saveUsername(context: Context, username: String) {
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    prefs.edit().putString("username", username).apply()
}

fun loadUsername(context: Context): String {
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    return prefs.getString("username", "Anonymous user") ?: "Anonymous user"
}

private fun saveImageUri(context: Context, uri: Uri) {
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    prefs.edit().putString("image_uri", uri.toString()).apply()
}

fun loadImageUri(context: Context): Uri? {
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val uriString = prefs.getString("image_uri", null)
    return if (!uriString.isNullOrEmpty()) Uri.parse(uriString) else null
}

private fun storageImage(context: Context, uri: Uri): Uri? {
    val contentResolver = context.contentResolver
    val inputStream: InputStream? = contentResolver.openInputStream(uri)
    if (inputStream != null) {

        val outputFile = File(context.filesDir, "profile_image.jpg")

        inputStream.use { input ->
            outputFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return Uri.fromFile(outputFile)
    } else {
        return null
    }
}



