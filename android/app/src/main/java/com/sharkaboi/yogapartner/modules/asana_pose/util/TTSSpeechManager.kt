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

            append("첫 자세는 다운독 자세입니다.")
            append(" 발로 부터 떨어진 곳에 두 손을 짚으세요.")
            append(" 허리와 다리는 펴고 엉덩이는 하늘을 향하세요.")

        }
        tts.speak(textToBeSpoken, QUEUE_FLUSH, null, System.currentTimeMillis().toString())
    }

    fun speakAsana(asanaClass: AsanaClass) {

        if (lastSpokenAsana == asanaClass) {
            return
        }

        lastSpokenAsana = asanaClass





        if(lastSpokenAsana==AsanaClass.adho_mukha_svanasana){
            val textToBeSpoken = buildString {

                //append(asanaClass.getFormattedString())
                append(" 잘하셨습니다 ")
                append(" 다음 자세는 고양이자세 입니다. ")
                append(" 무릎을 꿇고 양팔을 바닥에 짚으세요 ")


            }
            tts.speak(textToBeSpoken, QUEUE_FLUSH, null, System.currentTimeMillis().toString())
        }
        else if(lastSpokenAsana==AsanaClass.bidalasana){
            val textToBeSpoken = buildString {

                //append(asanaClass.getFormattedString())
                append(" 잘하셨습니다 ")
                append(" 다음 자세는 코브라자세 입니다. ")
                append(" 엎드린 뒤 두 손을 가슴 옆에 두고 상체를 일으켜 스트레칭 하세요 ")


            }
            tts.speak(textToBeSpoken, QUEUE_FLUSH, null, System.currentTimeMillis().toString())
        }
        else if(lastSpokenAsana==AsanaClass. bhujangasana){
            val textToBeSpoken = buildString {

                //append(asanaClass.getFormattedString())
                append(" 잘하셨습니다 ")
                append(" 다음 자세는 낙타자세입니다. ")
                append(" 바닥에 무릎을 꿇은 상태로 앉아서 다리는 엉덩이 넓이만큼 벌립니다. ")
                append(" 엉덩이를 공중으로 들어 올리면서 가슴과 골반을 앞으로 밀어주세요. ")

            }
            tts.speak(textToBeSpoken, QUEUE_FLUSH, null, System.currentTimeMillis().toString())
        }
        else if(lastSpokenAsana==AsanaClass. ustrasana){
            val textToBeSpoken = buildString {

                //append(asanaClass.getFormattedString())
                append(" 잘하셨습니다 ")
                append(" 다음 자세는 플랭크 입니다. ")
                append("바닥에 엉덩이를 대고 앉아 다리를 쭉 펴줍니다." )
                append(" 그리고 손바닥과 발바닥으로 지탱하면서 복근과 둔근에 힘을 주고, 골반을 바닥에서 천천히 들어 올리며 유지하세요")

            }
            tts.speak(textToBeSpoken, QUEUE_FLUSH, null, System.currentTimeMillis().toString())
        }
        else if(lastSpokenAsana==AsanaClass. phalakasana){
            val textToBeSpoken = buildString {

                //append(asanaClass.getFormattedString())
                append(" 잘하셨습니다 ")
                append(" 다음 시퀀스는 의자자세입니다. ")
                append(" 다운독 자세를 거쳐 몸을 일으키세요 ")
                append(" 선 자세에서 양발을 골반 너비만큼 벌리고 앉아줍니다.")
                append(" 합장한 뒤 두 팔을 대각선으로 뻗으세요 ")


            }
            tts.speak(textToBeSpoken, QUEUE_FLUSH, null, System.currentTimeMillis().toString())
        }
        else if(lastSpokenAsana==AsanaClass. utkatasana){
            val textToBeSpoken = buildString {

                //append(asanaClass.getFormattedString())
                append(" 잘하셨습니다 ")
                append(" 다음 시퀀스는 전사자세 일번입니다. ")
                append(" 서서 양발을 넓게 벌립니다 ")
                append(" 오른 발은 매트 앞으로, 왼발은 그대로 둡니다. ")
                append(" ,오른 무릎 구십도 앉아 줍니다. ")
                append(" 합장하여 양팔을 듭니다 ")


            }
            tts.speak(textToBeSpoken, QUEUE_FLUSH, null, System.currentTimeMillis().toString())
        }
        else if(lastSpokenAsana==AsanaClass. virabhadrasana_i){
            val textToBeSpoken = buildString {

                //append(asanaClass.getFormattedString())
                append(" 잘하셨습니다 ")
                append(" 다음 시퀀스는 전사자세 이번입니다. ")
                append(" 그 상태에서 옆을 보며 양팔을 양 옆으로 폅니다 ")
                append(" 유지합니다 ")



            }
            tts.speak(textToBeSpoken, QUEUE_FLUSH, null, System.currentTimeMillis().toString())
        }
        else if(lastSpokenAsana==AsanaClass. virabhadrasana_ii){
            val textToBeSpoken = buildString {

                //append(asanaClass.getFormattedString())
                append(" 잘하셨습니다 ")
                append(" 다음은 여신 자세입니다. ")
                append(" 그 상태에서 반대쪽 무릎도 구부려 유지합니다 ")




            }
            tts.speak(textToBeSpoken, QUEUE_FLUSH, null, System.currentTimeMillis().toString())
        }
        else if(lastSpokenAsana==AsanaClass.  utkata_konasana){
            val textToBeSpoken = buildString {

                //append(asanaClass.getFormattedString())
                append(" 잘하셨습니다 ")
                append(" 다음은 나무자세 입니다. ")
                append(" 매트 앞에 서서 합장한 뒤 한 다리를 접어 반대쪽 다리 무릎 옆에 둡니다. ")
                append(" 유지합니다. ")


            }
            tts.speak(textToBeSpoken, QUEUE_FLUSH, null, System.currentTimeMillis().toString())
        }
        else if(lastSpokenAsana==AsanaClass.vrikshasana){
            val textToBeSpoken = buildString {

                //append(asanaClass.getFormattedString())
                append(" 잘하셨습니다 ")
                append(" 정말 수고하셨습니다.")


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