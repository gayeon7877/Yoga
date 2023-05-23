package com.sharkaboi.yogapartner.modules.asana_pose.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.QUEUE_FLUSH
import com.google.common.io.Files.append
import com.sharkaboi.yogapartner.ml.classification.AsanaClass
import org.checkerframework.checker.units.qual.A
import java.util.*

class TTSSpeechManager(context: Context) {
    private var lastSpokenAsana: AsanaClass? = null
    private val tts = TextToSpeech(context.applicationContext) {}.apply {
        language = Locale.KOREAN
    }
    fun speak(){
        val textToBeSpoken = buildString {

            append("첫 자세는 아도무카스바아사나 입니다.")
            append(" 발로 부터 일미터가량 떨어진 곳에 두 손을 짚으세요.")
            append(" 허리와 다리는 펴고 엉덩이는 하늘을 향하세요.")

        }
        tts.speak(textToBeSpoken, QUEUE_FLUSH, null, System.currentTimeMillis().toString())
    }

    fun speakAsana(asanaClass: AsanaClass) {
        if (asanaClass == AsanaClass.UNKNOWN) {
            return
        }

        if (lastSpokenAsana == asanaClass) {
            return
        }

        lastSpokenAsana = asanaClass

        if (lastSpokenAsana==AsanaClass.UNKNOWN){
            val textToBeSpoken = buildString {

                append("첫 자세는 아도무카스바아사나 입니다.")
                append(" 발로 부터 일미터가량 떨어진 곳에 두 손을 짚으세요.")
                append(" 허리와 다리는 펴고 엉덩이는 하늘을 향하세요.")

            }
            tts.speak(textToBeSpoken, QUEUE_FLUSH, null, System.currentTimeMillis().toString())
        }
        else if(lastSpokenAsana==AsanaClass.adho_mukha_svanasana){
        val textToBeSpoken = buildString {

            //append(asanaClass.getFormattedString())
            append(" 잘하셨습니다 ")
            append(" 다음 자세는 비달라아사나 입니다. ")
            append(" 무릎을 꿇고 양팔을 바닥에 짚으세요 ")


        }
        tts.speak(textToBeSpoken, QUEUE_FLUSH, null, System.currentTimeMillis().toString())
        }
        else if(lastSpokenAsana==AsanaClass.bidalasana){
            val textToBeSpoken = buildString {

                //append(asanaClass.getFormattedString())
                append(" 잘하셨습니다 ")
                append(" 다음 자세는 부장가아사나 입니다. ")
                append(" 엎드린 뒤 두 손을 가슴 옆에 두고 상체를 일으켜 스트레칭 하세요 ")


            }
            tts.speak(textToBeSpoken, QUEUE_FLUSH, null, System.currentTimeMillis().toString())
        }
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