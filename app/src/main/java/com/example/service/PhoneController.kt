package com.example.service

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.provider.AlarmClock
import android.provider.MediaStore
import android.provider.Settings
import androidx.core.content.ContextCompat

data class InstalledAppInfo(
    val name: String,
    val packageName: String
)

data class BatteryInfo(
    val percentage: Int,
    val isCharging: Boolean,
    val statusText: String
)

class PhoneController(private val context: Context) {

    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private var isTorchOn = false

    fun isTorchActive(): Boolean = isTorchOn

    fun toggleFlashlight(enable: Boolean): Result<Boolean> {
        return try {
            if (cameraManager == null) {
                Result.failure(Exception("Camera hardware not available"))
            } else {
                val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
                    val characteristics = cameraManager.getCameraCharacteristics(id)
                    characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                }
                if (cameraId != null) {
                    cameraManager.setTorchMode(cameraId, enable)
                    isTorchOn = enable
                    Result.success(enable)
                } else {
                    Result.failure(Exception("No camera with flash detected"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun makePhoneCall(numberOrQuery: String): Result<String> {
        return try {
            val cleanNumber = numberOrQuery.replace("[^0-9+]".toRegex(), "")
            val targetUri = if (cleanNumber.isNotEmpty()) {
                Uri.parse("tel:$cleanNumber")
            } else {
                Uri.parse("tel:")
            }

            val hasCallPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED

            val intent = if (hasCallPermission && cleanNumber.isNotEmpty()) {
                Intent(Intent.ACTION_CALL, targetUri)
            } else {
                Intent(Intent.ACTION_DIAL, targetUri)
            }
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)

            val actionDesc = if (hasCallPermission && cleanNumber.isNotEmpty()) {
                "Placing direct call to $cleanNumber, Sir."
            } else {
                "Opening dialer for $numberOrQuery, Sir."
            }
            Result.success(actionDesc)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun sendSms(number: String, messageText: String): Result<String> {
        return try {
            val cleanNumber = number.replace("[^0-9+]".toRegex(), "")
            val uri = Uri.parse("smsto:$cleanNumber")
            val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
                putExtra("sms_body", messageText)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            Result.success("SMS dispatch window opened for $number with message: \"$messageText\"")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun sendWhatsAppMessage(number: String, messageText: String): Result<String> {
        return try {
            val cleanNumber = number.replace("[^0-9+]".toRegex(), "")
            val url = "https://api.whatsapp.com/send?phone=$cleanNumber&text=${Uri.encode(messageText)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                setPackage("com.whatsapp")
            }
            context.startActivity(intent)
            Result.success("Routing WhatsApp transmission to $number")
        } catch (_: Exception) {
            // Fallback to web or general view
            try {
                val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$number?text=${Uri.encode(messageText)}")).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(fallbackIntent)
                Result.success("Opening WhatsApp transmission interface.")
            } catch (e2: Exception) {
                Result.failure(e2)
            }
        }
    }

    fun openApp(query: String): Result<String> {
        val normalized = query.trim().lowercase()
        val pm = context.packageManager

        // Known direct package mappings
        val commonPackages = mapOf(
            "whatsapp" to "com.whatsapp",
            "youtube" to "com.google.android.youtube",
            "chrome" to "com.android.chrome",
            "browser" to "com.android.chrome",
            "camera" to "camera_intent",
            "photos" to "com.google.android.apps.photos",
            "gallery" to "gallery_intent",
            "settings" to "settings_intent",
            "maps" to "com.google.android.apps.maps",
            "gmail" to "com.google.android.gm",
            "mail" to "com.google.android.gm",
            "clock" to "clock_intent",
            "alarm" to "clock_intent",
            "calculator" to "calculator_intent",
            "instagram" to "com.instagram.android",
            "spotify" to "com.spotify.music",
            "telegram" to "org.telegram.messenger",
            "play store" to "com.android.vending",
            "store" to "com.android.vending"
        )

        // Check if query matches special system intents
        when {
            normalized.contains("camera") -> {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                if (intent.resolveActivity(pm) != null) {
                    context.startActivity(intent)
                    return Result.success("Engaging optical sensors: Camera online.")
                }
            }
            normalized.contains("setting") -> {
                val intent = Intent(Settings.ACTION_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                return Result.success("Opening Core System Settings.")
            }
            normalized.contains("calculator") -> {
                val calcIntent = pm.getLaunchIntentForPackage("com.google.android.calculator")
                    ?: pm.getLaunchIntentForPackage("com.android.calculator2")
                if (calcIntent != null) {
                    calcIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(calcIntent)
                    return Result.success("Launching Computation Matrix: Calculator active.")
                }
            }
            normalized.contains("clock") || normalized.contains("alarm") -> {
                val clockIntent = Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                if (clockIntent.resolveActivity(pm) != null) {
                    context.startActivity(clockIntent)
                    return Result.success("Opening Chrono Display: Alarms & Clock.")
                }
            }
        }

        // Try direct mapped package
        for ((key, pkg) in commonPackages) {
            if (normalized.contains(key)) {
                val intent = pm.getLaunchIntentForPackage(pkg)
                if (intent != null) {
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                    return Result.success("Protocol launched: $key is now running.")
                }
            }
        }

        // Scan installed packages dynamically
        try {
            val installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            for (app in installedApps) {
                if ((app.flags and ApplicationInfo.FLAG_SYSTEM) == 0 || app.packageName.contains("google") || app.packageName.contains("android")) {
                    val label = pm.getApplicationLabel(app).toString().lowercase()
                    if (label.contains(normalized) || normalized.contains(label)) {
                        val launchIntent = pm.getLaunchIntentForPackage(app.packageName)
                        if (launchIntent != null) {
                            launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(launchIntent)
                            return Result.success("Initiating $label protocol.")
                        }
                    }
                }
            }
        } catch (_: Exception) {}

        return Result.failure(Exception("Could not find matching app '$query' on this device."))
    }

    fun openSystemSettings(category: String): Result<String> {
        val action = when (category.lowercase()) {
            "wifi" -> Settings.ACTION_WIFI_SETTINGS
            "bluetooth" -> Settings.ACTION_BLUETOOTH_SETTINGS
            "sound", "volume" -> Settings.ACTION_SOUND_SETTINGS
            "display", "brightness" -> Settings.ACTION_DISPLAY_SETTINGS
            "battery" -> Intent.ACTION_POWER_USAGE_SUMMARY
            "date", "time" -> Settings.ACTION_DATE_SETTINGS
            else -> Settings.ACTION_SETTINGS
        }
        return try {
            val intent = Intent(action).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            Result.success("Accessing ${category.uppercase()} hardware parameters.")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun adjustVolume(increase: Boolean): Result<String> {
        val am = audioManager ?: return Result.failure(Exception("Audio control offline."))
        val direction = if (increase) AudioManager.ADJUST_RAISE else AudioManager.ADJUST_LOWER
        am.adjustStreamVolume(AudioManager.STREAM_MUSIC, direction, AudioManager.FLAG_SHOW_UI)
        val current = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val pct = (current * 100) / (if (max > 0) max else 1)
        return Result.success("Audio output level calibrated to $pct%.")
    }

    fun setMute(mute: Boolean): Result<String> {
        val am = audioManager ?: return Result.failure(Exception("Audio control offline."))
        am.ringerMode = if (mute) AudioManager.RINGER_MODE_SILENT else AudioManager.RINGER_MODE_NORMAL
        return Result.success(if (mute) "Audio output silenced. Silent protocol active." else "Audio output restored to normal.")
    }

    fun getBatteryInfo(): BatteryInfo {
        val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus: Intent? = context.registerReceiver(null, ifilter)
        val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val pct = if (level >= 0 && scale > 0) (level * 100) / scale else 85
        val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        val statusText = if (isCharging) "Arc Reactor Recharging" else "Power Cells Operational"
        return BatteryInfo(percentage = pct, isCharging = isCharging, statusText = statusText)
    }

    fun getQuickLaunchApps(): List<InstalledAppInfo> {
        val list = mutableListOf<InstalledAppInfo>()
        val pm = context.packageManager
        val candidates = listOf(
            "WhatsApp" to "com.whatsapp",
            "YouTube" to "com.google.android.youtube",
            "Chrome" to "com.android.chrome",
            "Maps" to "com.google.android.apps.maps",
            "Camera" to "camera_shortcut",
            "Settings" to "settings_shortcut",
            "Calculator" to "com.google.android.calculator",
            "Spotify" to "com.spotify.music"
        )
        for ((name, pkg) in candidates) {
            if (pkg.endsWith("_shortcut") || pm.getLaunchIntentForPackage(pkg) != null) {
                list.add(InstalledAppInfo(name, pkg))
            }
        }
        return list
    }
}
