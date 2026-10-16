package com.example.service

import com.example.data.ReminderEntity
import java.util.Calendar

sealed class JarvisIntent {
    data class OpenApp(val appQuery: String) : JarvisIntent()
    data class MakeCall(val target: String) : JarvisIntent()
    data class SendMessage(val recipient: String, val body: String, val isWhatsApp: Boolean = false) : JarvisIntent()
    data class ToggleFlashlight(val enable: Boolean) : JarvisIntent()
    data class AdjustVolume(val increase: Boolean) : JarvisIntent()
    data class SetMute(val mute: Boolean) : JarvisIntent()
    data class OpenSettings(val category: String) : JarvisIntent()
    data class CheckBattery(val query: String) : JarvisIntent()
    data class CreateReminder(val title: String, val timeInMillis: Long, val category: String) : JarvisIntent()
    data object ShowSchedule : JarvisIntent()
    data class Conversational(val speechReply: String) : JarvisIntent()
    data class Unknown(val rawText: String) : JarvisIntent()
}

data class CommandParseResult(
    val intent: JarvisIntent,
    val spokenFeedback: String,
    val intentLabel: String
)

class OfflineCommandEngine(
    private val phoneController: PhoneController
) {

    fun parseAndProcess(rawInput: String, operatorName: String = "Sky Team Sir"): CommandParseResult {
        val text = rawInput.trim()
        val lower = text.lowercase()

        // Name / Identity Queries
        if (lower.contains("my name") || lower.contains("mera naam") || lower.contains("who am i") || lower.contains("sky team")) {
            val reply = "You are $operatorName, Commander of this cyber terminal. I am at your command!"
            return CommandParseResult(JarvisIntent.Conversational(reply), reply, "IDENTITY")
        }

        // Cyber Security Terminal Commands
        if (containsAny(lower, "scan", "audit", "security audit", "security check", "vulnerability", "check threats")) {
            val batt = phoneController.getBatteryInfo()
            val feedback = "Cyber audit executed: Zero threats detected. Core power at ${batt.percentage}%. System hardened, $operatorName."
            return CommandParseResult(JarvisIntent.Conversational(feedback), feedback, "SECURITY_AUDIT")
        }
        if (containsAny(lower, "firewall", "netstat", "network security", "ports")) {
            val feedback = "Firewall protocol: ACTIVE. Inbound node filters engaged. AES-256 packet encryption enforced, $operatorName."
            return CommandParseResult(JarvisIntent.Conversational(feedback), feedback, "FIREWALL_STATUS")
        }
        if (containsAny(lower, "panic", "lockdown", "stealth mode", "ghost mode")) {
            phoneController.toggleFlashlight(false)
            phoneController.setMute(true)
            val feedback = "Stealth lockdown protocol active. Optical beacons disabled, telecommunications silenced, $operatorName."
            return CommandParseResult(JarvisIntent.SetMute(true), feedback, "STEALTH_LOCKDOWN")
        }

        // 1. Flashlight / Torch / Optical Beacon
        if (containsAny(lower, "torch 1", "torch on", "flashlight on", "torch jalao", "torch chalao", "turn on torch", "turn on flashlight", "light on", "batti jalao", "beacon on")) {
            val res = phoneController.toggleFlashlight(true)
            val feedback = if (res.isSuccess) "Flashlight beacon online, $operatorName. Optical output at 100%." else "Unable to activate optical flash."
            return CommandParseResult(JarvisIntent.ToggleFlashlight(true), feedback, "FLASHLIGHT_ON")
        }
        if (containsAny(lower, "torch 0", "torch off", "flashlight off", "torch band", "turn off torch", "turn off flashlight", "light off", "batti bujhao", "beacon off")) {
            val res = phoneController.toggleFlashlight(false)
            val feedback = if (res.isSuccess) "Flashlight beacon offline, $operatorName. Dark mode restored." else "Unable to deactivate torch."
            return CommandParseResult(JarvisIntent.ToggleFlashlight(false), feedback, "FLASHLIGHT_OFF")
        }

        // 2. Battery telemetry
        if (containsAny(lower, "battery", "power level", "charging", "arc reactor power", "kitni battery", "charge kitna")) {
            val batt = phoneController.getBatteryInfo()
            val feedback = "$operatorName, energy reserves stand at ${batt.percentage}%. ${batt.statusText}."
            return CommandParseResult(JarvisIntent.CheckBattery(lower), feedback, "BATTERY_CHECK")
        }

        // 3. Volume controls
        if (containsAny(lower, "volume up", "volume badhao", "awaaz badhao", "sound badhao", "raise volume", "increase volume")) {
            val res = phoneController.adjustVolume(true)
            val feedback = res.getOrDefault("Audio output amplified, $operatorName.")
            return CommandParseResult(JarvisIntent.AdjustVolume(true), feedback, "VOLUME_UP")
        }
        if (containsAny(lower, "volume down", "volume kam karo", "awaaz kam", "lower volume", "decrease volume")) {
            val res = phoneController.adjustVolume(false)
            val feedback = res.getOrDefault("Audio output reduced, $operatorName.")
            return CommandParseResult(JarvisIntent.AdjustVolume(false), feedback, "VOLUME_DOWN")
        }
        if (containsAny(lower, "mute", "silent mode", "chup ho jao", "silence", "shant ho jao")) {
            val res = phoneController.setMute(true)
            val feedback = res.getOrDefault("Device silenced, $operatorName.")
            return CommandParseResult(JarvisIntent.SetMute(true), feedback, "MUTE")
        }
        if (containsAny(lower, "unmute", "normal mode", "sound on")) {
            val res = phoneController.setMute(false)
            val feedback = res.getOrDefault("Audio normal profile restored, $operatorName.")
            return CommandParseResult(JarvisIntent.SetMute(false), feedback, "UNMUTE")
        }

        // 4. Calling / Phone
        if (lower.startsWith("call ") || lower.startsWith("phone ") || lower.startsWith("dial ") ||
            lower.contains("ko call karo") || lower.contains("ko phone lagao") || lower.contains("call lagao")) {
            val target = extractCallTarget(text)
            val res = phoneController.makePhoneCall(target)
            val feedback = res.getOrDefault("Routing telecommunication link to $target.")
            return CommandParseResult(JarvisIntent.MakeCall(target), feedback, "CALL")
        }

        // 5. WhatsApp direct transmission
        if (lower.contains("whatsapp")) {
            if (lower.contains("message") || lower.contains("bhejo") || lower.contains("send") || lower.contains("text")) {
                val (recipient, body) = extractMessageData(text, defaultRecipient = "Contact")
                val res = phoneController.sendWhatsAppMessage(recipient, body)
                val feedback = res.getOrDefault("Opening WhatsApp transmission interface.")
                return CommandParseResult(JarvisIntent.SendMessage(recipient, body, isWhatsApp = true), feedback, "WHATSAPP_MSG")
            } else {
                val res = phoneController.openApp("whatsapp")
                val feedback = res.getOrDefault("Accessing encrypted communication link: WhatsApp.")
                return CommandParseResult(JarvisIntent.OpenApp("whatsapp"), feedback, "APP_LAUNCH")
            }
        }

        // 6. Messaging / SMS
        if (lower.contains("sms") || lower.contains("message") || lower.contains("sandesh") || lower.contains("msg")) {
            if (lower.contains("send") || lower.contains("bhejo") || lower.contains("karo") || lower.contains("type")) {
                val (recipient, body) = extractMessageData(text, defaultRecipient = "")
                val res = phoneController.sendSms(recipient, body)
                val feedback = res.getOrDefault("SMS dispatch initialized.")
                return CommandParseResult(JarvisIntent.SendMessage(recipient, body, isWhatsApp = false), feedback, "SMS_MSG")
            }
        }

        // 7. Settings shortcuts
        if (containsAny(lower, "wifi", "wi-fi")) {
            val res = phoneController.openSystemSettings("wifi")
            val feedback = res.getOrDefault("Accessing wireless matrix settings.")
            return CommandParseResult(JarvisIntent.OpenSettings("wifi"), feedback, "SETTINGS_WIFI")
        }
        if (containsAny(lower, "bluetooth")) {
            val res = phoneController.openSystemSettings("bluetooth")
            val feedback = res.getOrDefault("Pairing subsystem settings online.")
            return CommandParseResult(JarvisIntent.OpenSettings("bluetooth"), feedback, "SETTINGS_BT")
        }
        if (containsAny(lower, "display settings", "brightness", "screen settings")) {
            val res = phoneController.openSystemSettings("display")
            val feedback = res.getOrDefault("Display calibration interface opened.")
            return CommandParseResult(JarvisIntent.OpenSettings("display"), feedback, "SETTINGS_DISPLAY")
        }

        // 8. Reminders & Schedule
        if (containsAny(lower, "schedule", "timetable", "dinchar", "aaj ka kaam", "today's tasks")) {
            return CommandParseResult(JarvisIntent.ShowSchedule, "Displaying today's tactical schedule, Sir.", "SCHEDULE_VIEW")
        }
        if (lower.startsWith("remind me") || lower.startsWith("reminder") || lower.contains("yaad dilana") || lower.contains("remind karo") || lower.contains("alarm")) {
            val (task, timeMillis, cat) = extractReminderInfo(text)
            return CommandParseResult(JarvisIntent.CreateReminder(task, timeMillis, cat), "Protocol registered: I will remind you to $task, Sir.", "REMINDER_CREATE")
        }

        // 9. App Launching ("Open ...", "... kholo", "... chalao", "launch ...")
        if (lower.startsWith("open ") || lower.startsWith("launch ") || lower.startsWith("start ") ||
            lower.endsWith(" kholo") || lower.endsWith(" chalao") || lower.endsWith(" open karo") ||
            lower.contains("open ") || lower.contains("kholo")) {
            val appQuery = extractAppQuery(text)
            val res = phoneController.openApp(appQuery)
            val feedback = res.getOrDefault("Initializing $appQuery protocol, Sir.")
            return CommandParseResult(JarvisIntent.OpenApp(appQuery), feedback, "APP_LAUNCH")
        }

        // 10. Greetings & Diagnostics
        if (containsAny(lower, "hello", "hi", "hey jarvis", "namaste", "pranam", "kya haal hai", "are you there", "wake up")) {
            val greetings = listOf(
                "Online and fully operational, $operatorName. How may I assist your mission today?",
                "Always at your service, $operatorName. All phone subsystems standing by.",
                "Namaste $operatorName! JARVIS systems are primed. Command me."
            )
            val reply = greetings.random()
            return CommandParseResult(JarvisIntent.Conversational(reply), reply, "GREETING")
        }
        if (containsAny(lower, "who are you", "tum kaun ho", "intro", "apna parichay")) {
            val reply = "I am JARVIS — Just A Rather Very Intelligent System. Dedicated AI assistant to $operatorName. Managing calls, applications, hardware, and tactical schedules."
            return CommandParseResult(JarvisIntent.Conversational(reply), reply, "IDENTITY")
        }
        if (containsAny(lower, "status", "diagnostic", "systems check", "all systems")) {
            val batt = phoneController.getBatteryInfo()
            val reply = "All protocols green, $operatorName. Core battery at ${batt.percentage}%. Speech engine calibrated. Awaiting your directive."
            return CommandParseResult(JarvisIntent.Conversational(reply), reply, "SYSTEM_CHECK")
        }

        // Default / Unknown command fallback
        // Try launching as app if it might be an app name
        if (text.split(" ").size in 1..2 && !lower.contains("what") && !lower.contains("why")) {
            val res = phoneController.openApp(text)
            if (res.isSuccess) {
                return CommandParseResult(JarvisIntent.OpenApp(text), "Executing $text protocol, $operatorName.", "APP_LAUNCH")
            }
        }

        val fallback = "Command registered: \"$text\". Directive executed for $operatorName."
        return CommandParseResult(JarvisIntent.Unknown(text), fallback, "UNKNOWN")
    }

    private fun containsAny(text: String, vararg targets: String): Boolean {
        return targets.any { text.contains(it) }
    }

    private fun extractCallTarget(text: String): String {
        var clean = text
        val prefixes = listOf("call ", "phone ", "dial ", "call karo ", "phone lagao ", "ko call karo", "ko phone karo")
        for (prefix in prefixes) {
            clean = clean.replace("(?i)$prefix".toRegex(), "")
        }
        return clean.trim().ifEmpty { "Voicemail" }
    }

    private fun extractAppQuery(text: String): String {
        var clean = text
        val filters = listOf(
            "open ", "launch ", "start ", " kholo", " chalao", " open karo",
            "mera ", "app ", "application ", "please "
        )
        for (f in filters) {
            clean = clean.replace("(?i)$f".toRegex(), "")
        }
        return clean.trim().ifEmpty { "Dashboard" }
    }

    private fun extractMessageData(text: String, defaultRecipient: String): Pair<String, String> {
        val parts = text.split(":", " say ", " bolna ", " bolo ")
        if (parts.size >= 2) {
            val target = extractCallTarget(parts[0])
            val msg = parts[1].trim()
            return Pair(target.ifEmpty { defaultRecipient }, msg)
        }
        // Extract digits if available
        val numberRegex = "\\+?[0-9]{7,15}".toRegex()
        val match = numberRegex.find(text)
        val number = match?.value ?: defaultRecipient
        val body = text.replace(number, "")
            .replace("(?i)(send|sms|message|bhejo|whatsapp|to)".toRegex(), "")
            .trim().ifEmpty { "Transmission from JARVIS" }
        return Pair(number, body)
    }

    private fun extractReminderInfo(text: String): Triple<String, Long, String> {
        val cal = Calendar.getInstance()
        cal.add(Calendar.HOUR_OF_DAY, 1) // default 1 hour from now

        val lower = text.lowercase()
        var category = "Reminder"
        if (lower.contains("alarm")) category = "Alarm"
        if (lower.contains("call")) category = "Call"

        var clean = text
            .replace("(?i)(remind me to|reminder|yaad dilana|set reminder for|set alarm for|remind me)".toRegex(), "")
            .trim()

        if (clean.isEmpty()) clean = "Tactical protocol check"

        return Triple(clean, cal.timeInMillis, category)
    }
}
