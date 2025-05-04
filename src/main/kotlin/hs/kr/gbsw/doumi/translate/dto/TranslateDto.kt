package hs.kr.gbsw.doumi.translate.dto

import org.springframework.web.multipart.MultipartFile

data class VoiceRequest(
    val audio: MultipartFile?,
    val beforeLang: String?,
    val afterLang: String?,
)

data class VoiceResponse(
    val result: String?,
)

data class TextRequest(
    val text: String?,
    val beforeLang: String?,
    val afterLang: String?,
)

data class TextResponse(
    val result: String?,
)