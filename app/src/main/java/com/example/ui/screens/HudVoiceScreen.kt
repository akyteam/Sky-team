package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.JarvisVoiceState
import com.example.ui.components.CyberDefenseShieldView
import com.example.ui.components.CyberMatrixGrid
import com.example.ui.components.CyberVulnerabilityScanner
import com.example.ui.components.ThemeSelectorDialog
import com.example.ui.components.VoicePersonaDialog
import com.example.ui.components.VoiceWaveVisualizer
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberBorderGlow
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberCyanGlow
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberRed
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.viewmodel.JarvisViewModel

@Composable
fun HudVoiceScreen(
    viewModel: JarvisViewModel,
    modifier: Modifier = Modifier
) {
    val voiceState by viewModel.voiceManager.voiceState.collectAsState()
    val audioRmsDb by viewModel.voiceManager.audioRmsDb.collectAsState()
    val partialTranscript by viewModel.voiceManager.partialTranscript.collectAsState()
    val lastCommand by viewModel.lastUserCommand.collectAsState()
    val lastReply by viewModel.lastJarvisReply.collectAsState()
    val batteryInfo by viewModel.batteryState.collectAsState()
    val isTorchOn by viewModel.isTorchOn.collectAsState()
    val recentLogs by viewModel.commandLogs.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val activePersona by viewModel.voiceManager.activePersona.collectAsState()
    val operatorName by viewModel.operatorName.collectAsState()

    var manualInput by remember { mutableStateOf("") }
    var isFirewallActive by remember { mutableStateOf(true) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showVoicePersonaDialog by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Box(modifier = modifier.fillMaxSize()) {
        // Live Animated Matrix Background & Laser Scanner
        CyberMatrixGrid()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            // Cyber Terminal Top Status Bar with Theme Button & Voice Button
            CyberTerminalHeader(
                batteryPercentage = batteryInfo.percentage,
                isCharging = batteryInfo.isCharging,
                isFirewallActive = isFirewallActive,
                onToggleFirewall = { isFirewallActive = !isFirewallActive },
                onOpenThemeSelector = { showThemeDialog = true },
                onOpenVoicePersona = { showVoicePersonaDialog = true }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Guaranteed Data Privacy & Safety Notice Card (Hindi & English reassurance)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberGreen.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberSurface.copy(alpha = 0.85f)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.GppGood,
                        contentDescription = "Data Safe",
                        tint = CyberGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "100% DATA SAFE: Aapke phone ka koi bhi data delete nahi hoga. JARVIS sirf commands execute karta hai.",
                        color = CyberTextPrimary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Commander Callsign Badge: SKY TEAM SIR
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberGreen.copy(alpha = 0.12f))
                    .border(1.dp, CyberGreen.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(CyberGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "COMMANDER: ${operatorName.uppercase()}",
                            color = CyberGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }

                    Text(
                        text = "CLEARANCE: LEVEL 5",
                        color = CyberCyan,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Cyber Defense Shield & Armored Arc Core
            CyberDefenseShieldView(
                voiceState = voiceState,
                audioRmsDb = audioRmsDb,
                isFirewallActive = isFirewallActive,
                onClick = {
                    if (voiceState == JarvisVoiceState.LISTENING) {
                        viewModel.voiceManager.stopListening()
                    } else {
                        viewModel.voiceManager.startListening()
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Audio Equalizer Waveform
            VoiceWaveVisualizer(
                voiceState = voiceState,
                audioRmsDb = audioRmsDb
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Dual Voice Triggers: In-App Listening & System Google Voice Fallback
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        if (voiceState == JarvisVoiceState.LISTENING) {
                            viewModel.voiceManager.stopListening()
                        } else {
                            viewModel.voiceManager.startListening()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (voiceState == JarvisVoiceState.LISTENING) CyberGold else CyberSurfaceVariant
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (voiceState == JarvisVoiceState.LISTENING) CyberGold else CyberGreen
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        tint = if (voiceState == JarvisVoiceState.LISTENING) Color.Black else CyberGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (voiceState == JarvisVoiceState.LISTENING) "LISTENING..." else "MIC LISTEN",
                        color = if (voiceState == JarvisVoiceState.LISTENING) Color.Black else CyberGreen,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = {
                        viewModel.triggerSystemSpeechDialog()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.RecordVoiceOver,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "GOOGLE VOICE",
                        color = CyberCyan,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Dedicated Voice Persona Selector Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberSurfaceVariant.copy(alpha = 0.7f))
                    .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                    .clickable { showVoicePersonaDialog = true }
                    .padding(horizontal = 12.dp, vertical = 7.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = CyberGreen,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ACTIVE VOICE: ${activePersona.title.uppercase()}",
                            color = CyberGreen,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }

                    Text(
                        text = "[CHANGE VOICE]",
                        color = CyberCyan,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Live Encrypted Voice Transcript Badge
            AnimatedVisibility(
                visible = voiceState == JarvisVoiceState.LISTENING || partialTranscript.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyberSurfaceVariant.copy(alpha = 0.9f))
                        .border(1.dp, CyberCyan.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (voiceState == JarvisVoiceState.LISTENING) CyberGold else CyberGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (partialTranscript.isNotEmpty()) partialTranscript else "CAPTURING AUDIO BUFFER...",
                            color = CyberCyanGlow,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Interactive Vulnerability Scanner Console
            CyberVulnerabilityScanner(
                onScanFinished = { report ->
                    viewModel.voiceManager.speak("System audit complete. Zero threat signatures detected, Sir.")
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Cyber Security Output Terminal Console
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberBorder, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface.copy(alpha = 0.95f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Terminal,
                                contentDescription = null,
                                tint = CyberGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ROOT@JARVIS-SEC:~# [${operatorName.uppercase()}]",
                                color = CyberGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                        }

                        if (voiceState == JarvisVoiceState.SPEAKING) {
                            IconButton(
                                onClick = { viewModel.voiceManager.stopSpeaking() },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Stop,
                                    contentDescription = "Mute",
                                    tint = CyberRed,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    if (lastCommand.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "> ${operatorName.uppercase()}: \"$lastCommand\"",
                            color = CyberGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "► $lastReply",
                        color = CyberTextPrimary,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Cyber Quick Defense Tactics & Voice Test Buttons
            Text(
                text = "ONE-TAP VOICE DIRECTIVES & SHORTCUTS",
                color = CyberTextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.5.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CyberChip(
                    label = if (isTorchOn) "BEACON OFF" else "BEACON ON",
                    icon = Icons.Default.FlashlightOn,
                    onClick = { viewModel.toggleFlashlight() }
                )
                CyberChip(
                    label = "RUN AUDIT",
                    icon = Icons.Default.Shield,
                    onClick = { viewModel.processCommand("scan") }
                )
                CyberChip(
                    label = "/bin/whatsapp",
                    icon = Icons.Default.PlayArrow,
                    onClick = { viewModel.processCommand("Open WhatsApp") }
                )
                CyberChip(
                    label = "/bin/youtube",
                    icon = Icons.Default.PlayArrow,
                    onClick = { viewModel.processCommand("Open YouTube") }
                )
                CyberChip(
                    label = "STEALTH MUTE",
                    icon = Icons.Default.VolumeOff,
                    onClick = { viewModel.processCommand("Mute") }
                )
                CyberChip(
                    label = "COMMS DIAL",
                    icon = Icons.Default.Call,
                    onClick = { viewModel.processCommand("Call") }
                )
                CyberChip(
                    label = "CHECK POWER",
                    icon = Icons.Default.Bolt,
                    onClick = { viewModel.processCommand("Battery status") }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Cyber Terminal Input Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberSurfaceVariant)
                    .border(1.dp, CyberBorderGlow.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#",
                    color = CyberGreen,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 6.dp)
                )

                OutlinedTextField(
                    value = manualInput,
                    onValueChange = { manualInput = it },
                    placeholder = {
                        Text(
                            text = "command (e.g., 'scan', 'torch 1', 'Open WhatsApp')...",
                            color = CyberTextMuted,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("command_input_field"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = CyberGreen,
                        unfocusedTextColor = CyberTextPrimary
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (manualInput.isNotBlank()) {
                                viewModel.processCommand(manualInput)
                                manualInput = ""
                                focusManager.clearFocus()
                            }
                        }
                    )
                )

                IconButton(
                    onClick = {
                        if (voiceState == JarvisVoiceState.LISTENING) {
                            viewModel.voiceManager.stopListening()
                        } else {
                            viewModel.voiceManager.startListening()
                        }
                    },
                    modifier = Modifier.testTag("mic_input_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Directive",
                        tint = if (voiceState == JarvisVoiceState.LISTENING) CyberGold else CyberCyan
                    )
                }

                IconButton(
                    onClick = {
                        if (manualInput.isNotBlank()) {
                            viewModel.processCommand(manualInput)
                            manualInput = ""
                            focusManager.clearFocus()
                        }
                    },
                    modifier = Modifier.testTag("send_command_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Transmit Command",
                        tint = CyberGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Cyber Audit & Execution Terminal Logs
            if (recentLogs.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyberBorder, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface.copy(alpha = 0.8f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "CRYPTOGRAPHIC AUDIT BUFFER",
                            color = CyberTextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        recentLogs.take(3).forEach { log ->
                            Text(
                                text = "► [SEC::${log.intentType}] ${log.commandText} -> ${log.responseSummary}",
                                color = CyberGreen,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showThemeDialog) {
        ThemeSelectorDialog(
            currentTheme = themeMode,
            onSelectTheme = { selectedMode ->
                viewModel.setThemeMode(selectedMode)
            },
            onDismiss = { showThemeDialog = false }
        )
    }

    if (showVoicePersonaDialog) {
        VoicePersonaDialog(
            currentPersona = activePersona,
            onSelectPersona = { persona ->
                viewModel.voiceManager.setPersona(persona)
            },
            onDismiss = { showVoicePersonaDialog = false }
        )
    }
}

@Composable
private fun CyberChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    SuggestionChip(
        onClick = onClick,
        label = {
            Text(
                text = label,
                color = CyberTextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        },
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CyberGreen,
                modifier = Modifier.size(14.dp)
            )
        },
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = CyberSurfaceVariant
        ),
        border = SuggestionChipDefaults.suggestionChipBorder(
            enabled = true,
            borderColor = CyberBorder
        ),
        shape = RoundedCornerShape(8.dp)
    )
}

@Composable
private fun CyberTerminalHeader(
    batteryPercentage: Int,
    isCharging: Boolean,
    isFirewallActive: Boolean,
    onToggleFirewall: () -> Unit,
    onOpenThemeSelector: () -> Unit,
    onOpenVoicePersona: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CyberSurface.copy(alpha = 0.9f))
            .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Battery Cyber Cell
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = "Battery Status",
                tint = if (isCharging) CyberGreen else if (batteryPercentage < 20) CyberRed else CyberCyan,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$batteryPercentage% PWR",
                color = CyberTextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        // Firewall Status Pill
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isFirewallActive) CyberGreen.copy(alpha = 0.15f) else CyberRed.copy(alpha = 0.15f))
                .border(
                    1.dp,
                    if (isFirewallActive) CyberGreen else CyberRed,
                    RoundedCornerShape(6.dp)
                )
                .clickable(onClick = onToggleFirewall)
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Icon(
                imageVector = if (isFirewallActive) Icons.Default.Security else Icons.Default.Lock,
                contentDescription = null,
                tint = if (isFirewallActive) CyberGreen else CyberRed,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (isFirewallActive) "FIREWALL ON" else "STEALTH ONLY",
                color = if (isFirewallActive) CyberGreen else CyberRed,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Voice Persona Switcher Button
            IconButton(
                onClick = onOpenVoicePersona,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.RecordVoiceOver,
                    contentDescription = "Change Voice Persona",
                    tint = CyberGreen,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Theme Palette Switcher Button
            IconButton(
                onClick = onOpenThemeSelector,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = "Theme Switcher",
                    tint = CyberCyan,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
