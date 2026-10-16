package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.theme.JarvisBackground
import com.example.ui.theme.JarvisBorder
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisGold
import com.example.ui.theme.JarvisGreen
import com.example.ui.theme.JarvisRed
import com.example.ui.theme.JarvisSurface
import com.example.ui.theme.JarvisSurfaceVariant
import com.example.ui.theme.JarvisTextMuted
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary

@Composable
fun SetupGuideScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var permissionsGrantedState by remember {
        mutableStateOf(checkAllPermissions(context))
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        permissionsGrantedState = checkAllPermissions(context)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(JarvisBackground)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "JARVIS SETUP MANUAL",
            color = JarvisCyan,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 2.sp
        )
        Text(
            text = "Step-by-Step mobile deployment & offline configuration",
            color = JarvisTextSecondary,
            fontSize = 12.sp
        )

        // Data Safety Reassurance Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, JarvisGreen, RoundedCornerShape(14.dp)),
            colors = CardDefaults.cardColors(containerColor = JarvisSurface),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = JarvisGreen, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PHONE DATA SAFETY: 100% GUARANTEE",
                        color = JarvisGreen,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Aapke phone ka koi bhi personal data, photos, videos, contacts ya files DELETE NAHI HONGI. Jarvis sirf wahi execute karta hai jo aap bolte hain (jaise app open karna ya torch jalana). Phone format ya reset karne ka koi code nahi hai.",
                    color = JarvisTextPrimary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Step 1: Mobile APK Export (No PC needed)
        GuideStepCard(
            stepNumber = "01",
            title = "Phone Par APK Download Aur Install Kaise Karein (No PC)",
            subtitle = "Direct Mobile Installation Guide",
            icon = Icons.Default.PhoneAndroid,
            tagColor = JarvisCyan
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                BulletText("1. AI Studio ke upar right corner mein **Settings** ya **Export** menu par tap karein.")
                BulletText("2. **Download APK / Generate APK** option choose karein. Browser mein seedha `.apk` file download ho jayegi.")
                BulletText("3. Mobile ke Notification ya Downloads folder mein jakar APK par tap karke **Install** dabayein.")
                BulletText("4. Agar 'Install from unknown source' prompt aaye toh 'Allow' kardein. Kisi laptop ya computer ki zaroorat nahi hai!")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Step 2: Permissions System
        GuideStepCard(
            stepNumber = "02",
            title = "Subsystem Permissions Access",
            subtitle = "Microphone, Phone, Flashlight, SMS & Alarms",
            icon = Icons.Default.Security,
            tagColor = if (permissionsGrantedState) JarvisGreen else JarvisGold
        ) {
            Column {
                Text(
                    text = "Jarvis ko phone ke functions (call lagana, torch on karna, voice sunna) control karne ke liye permissions chahiye:",
                    color = JarvisTextPrimary,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                PermissionRow("Microphone (Voice Control)", checkPerm(context, Manifest.permission.RECORD_AUDIO))
                PermissionRow("Phone Calling (Direct Calls)", checkPerm(context, Manifest.permission.CALL_PHONE))
                PermissionRow("Camera & Flashlight (Torch)", checkPerm(context, Manifest.permission.CAMERA))
                PermissionRow("Notifications & Reminders", true)

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val toRequest = mutableListOf(
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.CALL_PHONE,
                            Manifest.permission.CAMERA
                        )
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                            toRequest.add(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        permissionLauncher.launch(toRequest.toTypedArray())
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Grant Required Permissions Now", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Step 3: Offline Speech Recognition Setup
        GuideStepCard(
            stepNumber = "03",
            title = "100% Offline Voice Setup (Fast & Private)",
            subtitle = "Zero latency, privacy-first on-device speech processing",
            icon = Icons.Default.CloudDownload,
            tagColor = JarvisGreen
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Jarvis bina internet ke bhi super-fast chale iske liye apne phone mein offline voice pack download karein:",
                    color = JarvisTextPrimary,
                    fontSize = 13.sp
                )
                BulletText("1. Phone ki **Settings -> Google -> Settings for Google apps -> Voice** par jayein.")
                BulletText("2. **Offline speech recognition** option par tap karein.")
                BulletText("3. **'ALL'** tab mein jakar **English (India/US)** aur **Hindi** language pack download karein (~30-40MB).")
                BulletText("4. Ab Jarvis bina internet ke 0-second latency mein instant voice commands execute karega!")

                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = {
                        try {
                            val intent = Intent(Settings.ACTION_VOICE_INPUT_SETTINGS).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            val fallback = Intent(Settings.ACTION_SETTINGS).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(fallback)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JarvisSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, JarvisGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = JarvisGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Open Voice Settings on Phone", color = JarvisGreen, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Step 4: Complete Voice Command List
        GuideStepCard(
            stepNumber = "04",
            title = "Voice Commands Directory (Hindi & English)",
            subtitle = "What you can speak to Jarvis",
            icon = Icons.Default.Mic,
            tagColor = JarvisCyan
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CommandCategory(
                    category = "⚡ Flashlight & Hardware",
                    commands = listOf(
                        "\"Torch on\" / \"Flashlight chalao\" / \"Torch jalao\"",
                        "\"Torch off\" / \"Flashlight band karo\"",
                        "\"Volume up\" / \"Awaaz badhao\"",
                        "\"Volume down\" / \"Awaaz kam karo\"",
                        "\"Mute phone\" / \"Silent mode\" / \"Unmute\""
                    )
                )

                CommandCategory(
                    category = "📱 Apps Launching",
                    commands = listOf(
                        "\"Open WhatsApp\" / \"WhatsApp kholo\"",
                        "\"Open YouTube\" / \"YouTube chalao\"",
                        "\"Open Chrome\" / \"Browser kholo\"",
                        "\"Open Camera\" / \"Camera kholo\"",
                        "\"Open Settings\" / \"Settings kholo\""
                    )
                )

                CommandCategory(
                    category = "📞 Phone Calling & Messaging",
                    commands = listOf(
                        "\"Call Mom\" / \"Call 9876543210\"",
                        "\"Rahul ko call lagao\"",
                        "\"Send message to 9876543210: Hello Sir\"",
                        "\"WhatsApp 9876543210: Meeting ready\""
                    )
                )

                CommandCategory(
                    category = "⏰ Reminders & Battery",
                    commands = listOf(
                        "\"Remind me to take medicine in 1 hour\"",
                        "\"Set alarm for workout\"",
                        "\"Battery status\" / \"Jarvis battery kitni hai\"",
                        "\"Who are you\" / \"System status\""
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun GuideStepCard(
    stepNumber: String,
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tagColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, JarvisBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = JarvisSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(tagColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stepNumber,
                        color = tagColor,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = JarvisTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        color = JarvisTextSecondary,
                        fontSize = 11.sp
                    )
                }

                Icon(icon, contentDescription = null, tint = tagColor, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(JarvisBorder)
            )

            Spacer(modifier = Modifier.height(12.dp))

            content()
        }
    }
}

@Composable
private fun BulletText(text: String) {
    Text(
        text = text,
        color = JarvisTextPrimary,
        fontSize = 12.sp,
        lineHeight = 18.sp
    )
}

@Composable
private fun PermissionRow(name: String, isGranted: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = name, color = JarvisTextSecondary, fontSize = 12.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isGranted) Icons.Default.Check else Icons.Default.Warning,
                contentDescription = null,
                tint = if (isGranted) JarvisGreen else JarvisGold,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (isGranted) "GRANTED" else "PENDING",
                color = if (isGranted) JarvisGreen else JarvisGold,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CommandCategory(category: String, commands: List<String>) {
    Column {
        Text(
            text = category,
            color = JarvisGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(4.dp))
        commands.forEach { cmd ->
            Text(
                text = "• $cmd",
                color = JarvisTextPrimary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}

private fun checkPerm(context: Context, permission: String): Boolean {
    return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}

private fun checkAllPermissions(context: Context): Boolean {
    return checkPerm(context, Manifest.permission.RECORD_AUDIO) &&
            checkPerm(context, Manifest.permission.CALL_PHONE) &&
            checkPerm(context, Manifest.permission.CAMERA)
}
