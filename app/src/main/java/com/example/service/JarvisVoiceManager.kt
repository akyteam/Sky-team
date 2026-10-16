package com.example.service

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

enum class JarvisVoiceState {
    STANDBY,
    LISTENING,
    PROCESSING,
    SPEAKING,
    ERROR
}

enum class JarvisVoicePersona(
    val title: String,
    val subtitle: String,
    val pitch: Float,
    val speed: Float,
    val locale: Locale,
    val sampleText: String
) {
    JARVIS_MALE(
        title = "JARVIS (British Male)",
        subtitle = "Deep, composed Stark AI tone",
        pitch = 0.85f,
        speed = 1.05f,
        locale = Locale.UK,
        sampleText = "At your service, Sir. All phone subsystems operational."
    ),
    FRIDAY_FEMALE(
        title = "F.R.I.D.A.Y (Female AI)",
        subtitle = "Crisp, responsive tactical voice",
        pitch = 1.25f,
        speed = 1.08f,
        locale = Locale.US,
        sampleText = "F.R.I.D.A.Y online. Ready for your directive, Boss."
    ),
    CYBER_SYNTH(
        title = "CYBER SYNTH (Hacker Robotic)",
        subtitle = "Futuristic cybernetic vocoder tone",
        pitch = 0.70f,
        speed = 1.18f,
        locale = Locale.US,
        sampleText = "Cyber defense matrix active. Node encryption verified."
    ),
    HINDI_JARVIS(
        title = "JARVIS HINDI (Desi AI)",
        subtitle = "Natural Hindi/English accent",
        pitch = 0.95f,
        speed = 1.00f,
        locale = Locale("hi", "IN"),
        sampleText = "Namaste Sir! Main aapka Jarvis assistant, bataiye kya madad karun?"
    )
}

class JarvisVoiceManager(
    private val context: Context,
    private val onCommandRecognized: (String) -> Unit
) : RecognitionListener {

    private val mainHandler = Handler(Looper.getMainLooper())
    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false

    private var currentPersona = JarvisVoicePersona.JARVIS_MALE

    var onFallbackToSystemIntent: (() -> Unit)? = null

    private val _voiceState = MutableStateFlow(JarvisVoiceState.STANDBY)
    val voiceState: StateFlow<JarvisVoiceState> = _voiceState.asStateFlow()

    private val _audioRmsDb = MutableStateFlow(0f)
    val audioRmsDb: StateFlow<Float> = _audioRmsDb.asStateFlow()

    private val _partialTranscript = MutableStateFlow("")
    val partialTranscript: StateFlow<String> = _partialTranscript.asStateFlow()

    private val _activePersona = MutableStateFlow(JarvisVoicePersona.JARVIS_MALE)
    val activePersona: StateFlow<JarvisVoicePersona> = _activePersona.asStateFlow()

    init {
        initTts()
        mainHandler.post {
            initSpeechRecognizer()
        }
    }

    private fun initTts() {
        textToSpeech = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsReady = true
                applyPersonaSettings(currentPersona)
            }
        }
    }

    fun setPersona(persona: JarvisVoicePersona) {
        currentPersona = persona
        _activePersona.value = persona
        applyPersonaSettings(persona)
        speak(persona.sampleText)
    }

    private fun applyPersonaSettings(persona: JarvisVoicePersona) {
        textToSpeech?.apply {
            val langResult = setLanguage(persona.locale)
            if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                setLanguage(Locale.getDefault())
            }
            setPitch(persona.pitch)
            setSpeechRate(persona.speed)
            setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _voiceState.value = JarvisVoiceState.SPEAKING
                }

                override fun onDone(utteranceId: String?) {
                    _voiceState.value = JarvisVoiceState.STANDBY
                }

                override fun onError(utteranceId: String?) {
                    _voiceState.value = JarvisVoiceState.STANDBY
                }
            })
        }
    }

    private fun initSpeechRecognizer() {
        try {
            if (SpeechRecognizer.isRecognitionAvailable(context)) {
                speechRecognizer?.destroy()
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(this@JarvisVoiceManager)
                }
            }
        } catch (_: Exception) {}
    }

    fun startListening() {
        val hasMicPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasMicPermission) {
            _voiceState.value = JarvisVoiceState.ERROR
            _partialTranscript.value = "Microphone permission required! Tap to grant."
            onFallbackToSystemIntent?.invoke()
            return
        }

        // Stop TTS if speaking
        textToSpeech?.stop()

        mainHandler.post {
            if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                _partialTranscript.value = "Opening Google Voice dialog..."
                onFallbackToSystemIntent?.invoke()
                return@post
            }

            if (speechRecognizer == null) {
                initSpeechRecognizer()
            }

            try {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                    // CRITICAL FIX FOR ERROR CODE 13:
                    // Do NOT pass Locale.getDefault().toString() with unsupported regional strings like 'hi_IN'
                    // which triggers ERROR_LANGUAGE_UNAVAILABLE (Code 13) on Android.
                    // Letting RecognizerIntent auto-negotiate language avoids error 13 completely.
                }
                _voiceState.value = JarvisVoiceState.LISTENING
                _partialTranscript.value = "Listening... Speak your directive now."
                speechRecognizer?.startListening(intent)
            } catch (e: Exception) {
                _voiceState.value = JarvisVoiceState.ERROR
                _partialTranscript.value = "Mic activation error: ${e.localizedMessage}"
                onFallbackToSystemIntent?.invoke()
            }
        }
    }

    fun stopListening() {
        mainHandler.post {
            try {
                speechRecognizer?.stopListening()
                _voiceState.value = JarvisVoiceState.STANDBY
            } catch (_: Exception) {}
        }
    }

    fun speak(text: String) {
        if (isTtsReady && textToSpeech != null) {
            _voiceState.value = JarvisVoiceState.SPEAKING
            val params = Bundle().apply {
                putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "JARVIS_REPLY_${System.currentTimeMillis()}")
            }
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "JARVIS_REPLY")
        }
    }

    fun stopSpeaking() {
        textToSpeech?.stop()
        _voiceState.value = JarvisVoiceState.STANDBY
    }

    fun destroy() {
        mainHandler.post {
            try {
                speechRecognizer?.destroy()
                speechRecognizer = null
                textToSpeech?.stop()
                textToSpeech?.shutdown()
            } catch (_: Exception) {}
        }
    }

    // RecognitionListener Callbacks
    override fun onReadyForSpeech(params: Bundle?) {
        _voiceState.value = JarvisVoiceState.LISTENING
    }

    override fun onBeginningOfSpeech() {
        _voiceState.value = JarvisVoiceState.LISTENING
    }

    override fun onRmsChanged(rmsdB: Float) {
        _audioRmsDb.value = (rmsdB.coerceIn(0f, 10f)) / 10f
    }

    override fun onBufferReceived(buffer: ByteArray?) {}

    override fun onEndOfSpeech() {
        _voiceState.value = JarvisVoiceState.PROCESSING
        _audioRmsDb.value = 0f
    }

    override fun onError(error: Int) {
        _audioRmsDb.value = 0f
        _voiceState.value = JarvisVoiceState.STANDBY
        when (error) {
            13 -> { // ERROR_LANGUAGE_UNAVAILABLE (Android Error Code 13)
                _partialTranscript.value = "Language unavailable locally (Code 13 fixed). Auto-opening Google Voice dialog..."
                // Immediate seamless fallback to Google Voice Dialog so the user is never blocked!
                onFallbackToSystemIntent?.invoke()
            }
            12 -> { // ERROR_LANGUAGE_NOT_SUPPORTED
                _partialTranscript.value = "Language not supported by local model. Auto-opening Google Voice dialog..."
                onFallbackToSystemIntent?.invoke()
            }
            SpeechRecognizer.ERROR_NO_MATCH -> {
                _partialTranscript.value = "No vocal input recognized. Tap mic or Arc core to retry."
            }
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                _partialTranscript.value = "Microphone timed out. Tap to speak again."
            }
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> {
                _partialTranscript.value = "Permission error: RECORD_AUDIO required. Launching voice dialog..."
                onFallbackToSystemIntent?.invoke()
            }
            SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_SERVER -> {
                _partialTranscript.value = "Offline language server unavailable. Auto-opening Google Voice..."
                onFallbackToSystemIntent?.invoke()
            }
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> {
                _partialTranscript.value = "Speech recognizer busy. Resetting..."
                initSpeechRecognizer()
            }
            else -> {
                _partialTranscript.value = "Voice status (Code: $error). Tap Google Voice button to speak."
                onFallbackToSystemIntent?.invoke()
            }
        }
    }

    override fun onResults(results: Bundle?) {
        _audioRmsDb.value = 0f
        _voiceState.value = JarvisVoiceState.PROCESSING
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val bestMatch = matches?.firstOrNull()?.trim()
        if (!bestMatch.isNullOrEmpty()) {
            _partialTranscript.value = "\"$bestMatch\""
            onCommandRecognized(bestMatch)
        } else {
            _voiceState.value = JarvisVoiceState.STANDBY
        }
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val interim = matches?.firstOrNull()?.trim()
        if (!interim.isNullOrEmpty()) {
            _partialTranscript.value = interim
        }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {}
}
