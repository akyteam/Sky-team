package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberBorderGlow
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberRed
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.viewmodel.JarvisViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DeviceControlsScreen(
    viewModel: JarvisViewModel,
    modifier: Modifier = Modifier
) {
    val isTorchOn by viewModel.isTorchOn.collectAsState()
    val scrollState = rememberScrollState()

    var callInput by remember { mutableStateOf("") }
    var smsNumber by remember { mutableStateOf("") }
    var smsBody by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Terminal, contentDescription = null, tint = CyberGreen, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "CYBER LAUNCHER & NODE MATRIX",
                color = CyberCyan,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.5.sp
            )
        }
        Text(
            text = "Direct executable launcher & device payload deck",
            color = CyberTextSecondary,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Cyber Executable App Drawer Grid
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyberBorder, RoundedCornerShape(14.dp)),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SYSTEM EXECUTABLES (/bin/)",
                        color = CyberGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "STATUS: DEPLOYED",
                        color = CyberCyan,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CyberAppDeckItem(
                        name = "/bin/whatsapp",
                        category = "COMMS",
                        icon = Icons.AutoMirrored.Filled.Send,
                        tint = CyberGreen
                    ) {
                        viewModel.processCommand("Open WhatsApp")
                    }
                    CyberAppDeckItem(
                        name = "/bin/youtube",
                        category = "MEDIA",
                        icon = Icons.Default.PlayCircle,
                        tint = CyberRed
                    ) {
                        viewModel.processCommand("Open YouTube")
                    }
                    CyberAppDeckItem(
                        name = "/bin/chrome",
                        category = "NET_EXPLORER",
                        icon = Icons.Default.Public,
                        tint = CyberCyan
                    ) {
                        viewModel.processCommand("Open Chrome")
                    }
                    CyberAppDeckItem(
                        name = "/bin/camera",
                        category = "OPTICAL_SYS",
                        icon = Icons.Default.CameraAlt,
                        tint = CyberGold
                    ) {
                        viewModel.processCommand("Open Camera")
                    }
                    CyberAppDeckItem(
                        name = "/bin/maps",
                        category = "GEO_SAT",
                        icon = Icons.Default.Map,
                        tint = CyberGreen
                    ) {
                        viewModel.processCommand("Open Maps")
                    }
                    CyberAppDeckItem(
                        name = "/bin/calc",
                        category = "CRYPTO_MATH",
                        icon = Icons.Default.Calculate,
                        tint = CyberCyan
                    ) {
                        viewModel.processCommand("Open Calculator")
                    }
                    CyberAppDeckItem(
                        name = "/bin/settings",
                        category = "CORE_SYS",
                        icon = Icons.Default.Settings,
                        tint = CyberTextSecondary
                    ) {
                        viewModel.processCommand("Open Settings")
                    }
                    CyberAppDeckItem(
                        name = "/bin/music",
                        category = "AUDIO_STREAM",
                        icon = Icons.Default.MusicNote,
                        tint = CyberGold
                    ) {
                        viewModel.processCommand("Open Spotify")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Hardware Controls Matrix
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyberBorder, RoundedCornerShape(14.dp)),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "HARDWARE & STEALTH CONTROLS",
                    color = CyberTextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CyberTile(
                        title = "Beacon",
                        status = if (isTorchOn) "ONLINE" else "OFFLINE",
                        icon = Icons.Default.FlashlightOn,
                        isActive = isTorchOn,
                        activeColor = CyberGold,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.toggleFlashlight() }
                    )
                    CyberTile(
                        title = "Gain +",
                        status = "RAISE",
                        icon = Icons.AutoMirrored.Filled.VolumeUp,
                        isActive = false,
                        activeColor = CyberCyan,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.processCommand("Volume up") }
                    )
                    CyberTile(
                        title = "Gain -",
                        status = "LOWER",
                        icon = Icons.Default.VolumeMute,
                        isActive = false,
                        activeColor = CyberCyan,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.processCommand("Volume down") }
                    )
                    CyberTile(
                        title = "Jammer",
                        status = "SILENT",
                        icon = Icons.Default.VolumeOff,
                        isActive = false,
                        activeColor = CyberRed,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.processCommand("Mute") }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CyberSettingsPill("Wi-Fi Net", Icons.Default.Wifi, Modifier.weight(1f)) {
                        viewModel.phoneController.openSystemSettings("wifi")
                    }
                    CyberSettingsPill("Bluetooth", Icons.Default.Bluetooth, Modifier.weight(1f)) {
                        viewModel.phoneController.openSystemSettings("bluetooth")
                    }
                    CyberSettingsPill("Display", Icons.Default.BrightnessMedium, Modifier.weight(1f)) {
                        viewModel.phoneController.openSystemSettings("display")
                    }
                    CyberSettingsPill("Core Setup", Icons.Default.Settings, Modifier.weight(1f)) {
                        viewModel.phoneController.openSystemSettings("system")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Encrypted Communications Terminal
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyberBorder, RoundedCornerShape(14.dp)),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "TELECOM & PACKET DISPATCH",
                    color = CyberTextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = callInput,
                        onValueChange = { callInput = it },
                        placeholder = {
                            Text("target: number or contact...", color = CyberTextMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("call_input_field"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberGreen,
                            unfocusedBorderColor = CyberBorder,
                            focusedTextColor = CyberGreen,
                            unfocusedTextColor = CyberTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (callInput.isNotBlank()) {
                                viewModel.processCommand("Call $callInput")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("call_action_button")
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "Call", tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("DIAL", color = Color.Black, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = smsNumber,
                    onValueChange = { smsNumber = it },
                    placeholder = {
                        Text("recipient node...", color = CyberTextMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = CyberCyan,
                        unfocusedTextColor = CyberTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = smsBody,
                    onValueChange = { smsBody = it },
                    placeholder = {
                        Text("packet payload text...", color = CyberTextMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = CyberCyan,
                        unfocusedTextColor = CyberTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (smsNumber.isNotBlank()) {
                                viewModel.processCommand("SMS $smsNumber $smsBody")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Message, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("DISPATCH SMS", color = CyberCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }

                    Button(
                        onClick = {
                            if (smsNumber.isNotBlank()) {
                                viewModel.processCommand("WhatsApp $smsNumber $smsBody")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberGreen.copy(alpha = 0.2f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = CyberGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("DISPATCH WA", color = CyberGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun CyberTile(
    title: String,
    status: String,
    icon: ImageVector,
    isActive: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isActive) activeColor.copy(alpha = 0.2f) else CyberSurfaceVariant)
            .border(
                1.dp,
                if (isActive) activeColor else CyberBorder,
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isActive) activeColor else CyberCyan,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                color = CyberTextPrimary,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = status,
                color = if (isActive) activeColor else CyberTextMuted,
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CyberSettingsPill(
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(CyberSurfaceVariant)
            .border(1.dp, CyberBorder, RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = label, tint = CyberCyan, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text(label, color = CyberTextSecondary, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
private fun CyberAppDeckItem(
    name: String,
    category: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(CyberSurfaceVariant)
            .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = name, tint = tint, modifier = Modifier.size(14.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = name,
                    color = CyberTextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = category,
                    color = CyberTextMuted,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
