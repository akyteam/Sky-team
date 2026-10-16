package com.example

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.DeviceControlsScreen
import com.example.ui.screens.HudVoiceScreen
import com.example.ui.screens.ScheduleScreen
import com.example.ui.screens.SetupGuideScreen
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.JarvisTab
import com.example.viewmodel.JarvisViewModel
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: JarvisViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsState()

            MyApplicationTheme(themeMode = themeMode) {
                JarvisAppRoot(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun JarvisAppRoot(viewModel: JarvisViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val requestSystemSpeech by viewModel.requestSystemSpeech.collectAsState()
    val context = LocalContext.current

    // System Speech Recognizer Intent Launcher (Foolproof fallback working on 100% of Android devices)
    val systemSpeechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val spokenMatches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val topSpokenText = spokenMatches?.firstOrNull()?.trim()
            if (!topSpokenText.isNullOrEmpty()) {
                viewModel.processCommand(topSpokenText)
            }
        }
    }

    // Trigger system speech dialog when requested
    LaunchedEffect(requestSystemSpeech) {
        if (requestSystemSpeech != null) {
            try {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_PROMPT, "JARVIS Cyber Assistant - Listening...")
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toString())
                }
                systemSpeechLauncher.launch(intent)
            } catch (_: Exception) {}
        }
    }

    // Request vital mic & notification permissions on first launch
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { /* Handled gracefully */ }

    LaunchedEffect(Unit) {
        val permissions = mutableListOf(Manifest.permission.RECORD_AUDIO)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        val needed = permissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
        if (needed.isNotEmpty()) {
            permissionLauncher.launch(needed.toTypedArray())
        }
    }

    // Handle back button: return to HUD tab if inside sub-screens
    BackHandler(enabled = currentTab != JarvisTab.HUD) {
        viewModel.setTab(JarvisTab.HUD)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        containerColor = CyberBackground,
        bottomBar = {
            JarvisBottomBar(
                currentTab = currentTab,
                onSelectTab = { viewModel.setTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                JarvisTab.HUD -> HudVoiceScreen(viewModel = viewModel)
                JarvisTab.CONTROLS -> DeviceControlsScreen(viewModel = viewModel)
                JarvisTab.SCHEDULE -> ScheduleScreen(viewModel = viewModel)
                JarvisTab.SETUP_GUIDE -> SetupGuideScreen()
            }
        }
    }
}

@Composable
private fun JarvisBottomBar(
    currentTab: JarvisTab,
    onSelectTab: (JarvisTab) -> Unit
) {
    NavigationBar(
        containerColor = CyberSurface,
        tonalElevation = 8.dp,
        modifier = Modifier
            .border(
                width = 1.dp,
                color = CyberBorder,
                shape = androidx.compose.ui.graphics.RectangleShape
            )
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        NavigationBarItem(
            selected = currentTab == JarvisTab.HUD,
            onClick = { onSelectTab(JarvisTab.HUD) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Cyber Defense HUD"
                )
            },
            label = {
                Text(
                    text = "DEFENSE",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = CyberGreen,
                indicatorColor = CyberGreen,
                unselectedIconColor = CyberTextSecondary,
                unselectedTextColor = CyberTextMuted
            )
        )

        NavigationBarItem(
            selected = currentTab == JarvisTab.CONTROLS,
            onClick = { onSelectTab(JarvisTab.CONTROLS) },
            icon = {
                Icon(
                    imageVector = Icons.Default.PhoneAndroid,
                    contentDescription = "Cyber App Launcher"
                )
            },
            label = {
                Text(
                    text = "LAUNCHER",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = CyberCyan,
                indicatorColor = CyberCyan,
                unselectedIconColor = CyberTextSecondary,
                unselectedTextColor = CyberTextMuted
            )
        )

        NavigationBarItem(
            selected = currentTab == JarvisTab.SCHEDULE,
            onClick = { onSelectTab(JarvisTab.SCHEDULE) },
            icon = {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Schedule"
                )
            },
            label = {
                Text(
                    text = "SCHEDULE",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = CyberCyan,
                indicatorColor = CyberCyan,
                unselectedIconColor = CyberTextSecondary,
                unselectedTextColor = CyberTextMuted
            )
        )

        NavigationBarItem(
            selected = currentTab == JarvisTab.SETUP_GUIDE,
            onClick = { onSelectTab(JarvisTab.SETUP_GUIDE) },
            icon = {
                Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = "Setup Manual"
                )
            },
            label = {
                Text(
                    text = "MANUAL",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = CyberCyan,
                indicatorColor = CyberCyan,
                unselectedIconColor = CyberTextSecondary,
                unselectedTextColor = CyberTextMuted
            )
        )
    }
}
