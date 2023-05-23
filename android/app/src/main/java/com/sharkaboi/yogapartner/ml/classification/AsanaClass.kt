package com.sharkaboi.yogapartner.ml.classification

@Suppress("EnumEntryName", "SpellCheckingInspection")
enum class AsanaClass {
    UNKNOWN,
    adho_mukha_svanasana,
    bhujangasana,
    bidalasana,
    phalakasana,
    ustrasana,
    utkatasana,
    utkata_konasana,
    virabhadrasana_i,
    virabhadrasana_ii,
    vrikshasana;

    fun getFormattedString(): String {
        val string = when (this) {
            UNKNOWN -> "알 수 없음"
            adho_mukha_svanasana -> "다운독 자세"
            bhujangasana -> "코브라 자세"
            phalakasana -> "플랭크 자세"
            ustrasana -> "낙타 자세"
            utkatasana -> "의자 자세"
            virabhadrasana_i -> "전사 자세 1"
            virabhadrasana_ii -> "전사 자세 2"
            utkata_konasana -> "여신 자세"
            bidalasana -> "고양이 자세"
            vrikshasana -> "나무 자세"
        }
        return string
    }
}