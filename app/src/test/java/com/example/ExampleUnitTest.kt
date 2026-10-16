package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.service.JarvisIntent
import com.example.service.OfflineCommandEngine
import com.example.service.PhoneController
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ExampleUnitTest {

    @Test
    fun testOfflineCommandParsingTorch() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val controller = PhoneController(context)
        val engine = OfflineCommandEngine(controller)

        val result1 = engine.parseAndProcess("torch on")
        assertEquals("FLASHLIGHT_ON", result1.intentLabel)

        val resultHindi = engine.parseAndProcess("torch chalao")
        assertEquals("FLASHLIGHT_ON", resultHindi.intentLabel)

        val resultOff = engine.parseAndProcess("flashlight off")
        assertEquals("FLASHLIGHT_OFF", resultOff.intentLabel)
    }

    @Test
    fun testOfflineCommandParsingApps() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val controller = PhoneController(context)
        val engine = OfflineCommandEngine(controller)

        val res = engine.parseAndProcess("Open WhatsApp")
        assertEquals("APP_LAUNCH", res.intentLabel)
        assertTrue(res.intent is JarvisIntent.OpenApp)
    }

    @Test
    fun testOfflineCommandParsingBattery() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val controller = PhoneController(context)
        val engine = OfflineCommandEngine(controller)

        val res = engine.parseAndProcess("battery status")
        assertEquals("BATTERY_CHECK", res.intentLabel)
    }

    @Test
    fun testOperatorCallsign() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val controller = PhoneController(context)
        val engine = OfflineCommandEngine(controller)

        val res = engine.parseAndProcess("who am i", "Sky Team Sir")
        assertEquals("IDENTITY", res.intentLabel)
        assertTrue(res.spokenFeedback.contains("Sky Team Sir"))
    }
}
