package com.sharkaboi.yogapartner.modules

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import com.sharkaboi.yogapartner.ml.classification.AsanaClass
import java.util.*

class YogaClass (context: Context) {
    private var lastSpokenAsana: AsanaClass? = null
    private val tts = TextToSpeech(context.applicationContext) {}.apply {
        language = Locale.US
    }





    fun stop() {
        Log.d("test","stop")
    }

    fun shutdown() {
        Log.d("test","shutdown")
    }
}