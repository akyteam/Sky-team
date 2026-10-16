package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.JarvisDatabase
import com.example.data.ReminderEntity
import com.example.data.VoiceCommandLog
import com.example.service.BatteryInfo
import com.example.service.JarvisIntent
import com.example.service.JarvisVoiceManager
import com.example.service.JarvisVoiceState
import com.example.service.OfflineCommandEngine
import com.example.service.PhoneController
import com.example.ui.theme.CyberThemeMode
import com.example.util.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class JarvisTab {
    HUD,
    CONTROLS,
    SCHEDULE,
    SETUP_GUIDE
}

class JarvisViewModel(application: Application) : AndroidViewModel(application) {

    private val db = JarvisDatabase.getDatabase(application)
    private val dao = db.jarvisDao()
    private val prefs = application.getSharedPreferences("jarvis_settings", Context.MODE_PRIVATE)

    val phoneController = PhoneController(application)
    val commandEngine = OfflineCommandEngine(phoneController)

    lateinit var voiceManager: JarvisVoiceManager
        private set

    private val _themeMode = MutableStateFlow(loadSavedTheme())
    val themeMode: StateFlow<CyberThemeMode> = _themeMode.asStateFlow()

    private val _operatorName = MutableStateFlow(
        prefs.getString("pref_operator_name", "Sky Team Sir") ?: "Sky Team Sir"
    )
    val operatorName: StateFlow<String> = _operatorName.asStateFlow()

    private val _requestSystemSpeech = MutableStateFlow<Long?>(null)
    val requestSystemSpeech: StateFlow<Long?> = _requestSystemSpeech.asStateFlow()

    private val _currentTab = MutableStateFlow(JarvisTab.HUD)
    val currentTab: StateFlow<JarvisTab> = _currentTab.asStateFlow()

    private val _lastUserCommand = MutableStateFlow("")
    val lastUserCommand: StateFlow<String> = _lastUserCommand.asStateFlow()

    private val _lastJarvisReply = MutableStateFlow("Jarvis online, Sky Team Sir. Cyber defense matrix primed. Awaiting your directive.")
    val lastJarvisReply: StateFlow<String> = _lastJarvisReply.asStateFlow()

    private val _batteryState = MutableStateFlow(phoneController.getBatteryInfo())
    val batteryState: StateFlow<BatteryInfo> = _batteryState.asStateFlow()

    private val _isTorchOn = MutableStateFlow(false)
    val isTorchOn: StateFlow<Boolean> = _isTorchOn.asStateFlow()

    val reminders: StateFlow<List<ReminderEntity>> = dao.getAllReminders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val commandLogs: StateFlow<List<VoiceCommandLog>> = dao.getRecentCommandLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        voiceManager = JarvisVoiceManager(application) { recognizedText ->
            processCommand(recognizedText)
        }
        voiceManager.onFallbackToSystemIntent = {
            triggerSystemSpeechDialog()
        }
        refreshBattery()
    }

    fun setOperatorName(name: String) {
        val clean = name.trim().ifEmpty { "Sky Team Sir" }
        _operatorName.value = clean
        prefs.edit().putString("pref_operator_name", clean).apply()
        val msg = "Commander callsign confirmed: $clean. Standing by for orders."
        _lastJarvisReply.value = msg
        voiceManager.speak(msg)
    }

    private fun loadSavedTheme(): CyberThemeMode {
        val saved = prefs.getString("pref_theme_mode", CyberThemeMode.ARC_CYAN.name)
        return try {
            CyberThemeMode.valueOf(saved ?: CyberThemeMode.ARC_CYAN.name)
        } catch (_: Exception) {
            CyberThemeMode.ARC_CYAN
        }
    }

    fun setThemeMode(mode: CyberThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString("pref_theme_mode", mode.name).apply()
        val msg = "Theme calibrated to ${mode.title}, ${_operatorName.value}."
        _lastJarvisReply.value = msg
        voiceManager.speak(msg)
    }

    fun triggerSystemSpeechDialog() {
        _requestSystemSpeech.value = System.currentTimeMillis()
    }

    fun setTab(tab: JarvisTab) {
        _currentTab.value = tab
    }

    fun refreshBattery() {
        _batteryState.value = phoneController.getBatteryInfo()
    }

    fun processCommand(commandText: String) {
        if (commandText.isBlank()) return
        _lastUserCommand.value = commandText

        viewModelScope.launch {
            val result = commandEngine.parseAndProcess(commandText, _operatorName.value)
            _lastJarvisReply.value = result.spokenFeedback

            // Voice feedback
            voiceManager.speak(result.spokenFeedback)

            // If reminder intent, save in DB and schedule
            if (result.intent is JarvisIntent.CreateReminder) {
                val newReminder = ReminderEntity(
                    title = result.intent.title,
                    timeInMillis = result.intent.timeInMillis,
                    category = result.intent.category
                )
                val id = dao.insertReminder(newReminder)
                ReminderScheduler.schedule(getApplication(), newReminder.copy(id = id))
            } else if (result.intent is JarvisIntent.ToggleFlashlight) {
                _isTorchOn.value = result.intent.enable
            } else if (result.intent is JarvisIntent.ShowSchedule) {
                _currentTab.value = JarvisTab.SCHEDULE
            }

            // Save log in Room
            dao.insertCommandLog(
                VoiceCommandLog(
                    commandText = commandText,
                    responseSummary = result.spokenFeedback,
                    intentType = result.intentLabel,
                    success = true
                )
            )

            refreshBattery()
        }
    }

    fun toggleFlashlight() {
        val next = !_isTorchOn.value
        val res = phoneController.toggleFlashlight(next)
        if (res.isSuccess) {
            _isTorchOn.value = next
            val msg = if (next) "Flashlight beacon engaged, Sir." else "Flashlight turned off."
            _lastJarvisReply.value = msg
            voiceManager.speak(msg)
        }
    }

    fun addManualReminder(title: String, timeInMillis: Long, category: String) {
        viewModelScope.launch {
            val reminder = ReminderEntity(
                title = title,
                timeInMillis = timeInMillis,
                category = category
            )
            val id = dao.insertReminder(reminder)
            ReminderScheduler.schedule(getApplication(), reminder.copy(id = id))
            val msg = "Protocol registered: $title scheduled."
            _lastJarvisReply.value = msg
            voiceManager.speak(msg)
        }
    }

    fun toggleReminderCompleted(reminder: ReminderEntity) {
        viewModelScope.launch {
            val updated = reminder.copy(isCompleted = !reminder.isCompleted)
            dao.updateReminder(updated)
            if (updated.isCompleted) {
                ReminderScheduler.cancel(getApplication(), reminder.id)
            }
        }
    }

    fun deleteReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            ReminderScheduler.cancel(getApplication(), reminder.id)
            dao.deleteReminder(reminder)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            dao.clearLogs()
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.destroy()
    }
}
