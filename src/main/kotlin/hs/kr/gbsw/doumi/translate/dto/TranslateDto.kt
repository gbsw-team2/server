package hs.kr.gbsw.doumi.translate.dto

data class VoiceRequest(
    val beforeLang: String?,
    val afterLang: String?,
)

data class TextRequest(
    val text: String?,
    val beforeLang: String?,
    val afterLang: String?,
)