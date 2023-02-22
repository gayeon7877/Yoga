package com.sharkaboi.yogapartner.modules.asana_pose.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.QUEUE_FLUSH
import com.sharkaboi.yogapartner.ml.classification.AsanaClass
import org.checkerframework.checker.units.qual.A
import java.util.*

class TTSSpeechManager(context: Context) {
    private var lastSpokenAsana: AsanaClass? = null
    private val tts = TextToSpeech(context.applicationContext) {}.apply {
        language = Locale.KOREAN
    }

    fun speakAsana(asanaClass: AsanaClass) {
        if (asanaClass == AsanaClass.UNKNOWN) {
            return
        }

        if (lastSpokenAsana == asanaClass) {
            return
        }

        lastSpokenAsana = asanaClass

        val textToBeSpoken = buildString {

            append(asanaClass.getFormattedString())
            append("를 잘하고 계십니다.")
        }
        tts.speak(textToBeSpoken, QUEUE_FLUSH, null, System.currentTimeMillis().toString())
    }

    fun speakNextAsana(asanaClass: AsanaClass){
        val textToBeSpoken = buildString {
            append("다음 자세는")
            append(asanaClass.getFormattedString())
            append("입니다. 10초 안에 자세를 변경해주세요")
        }
        tts.speak(textToBeSpoken, QUEUE_FLUSH, null, System.currentTimeMillis().toString())
    }

    fun stop() {
        tts.stop()
    }

    fun shutdown() {
        tts.shutdown()
    }
}