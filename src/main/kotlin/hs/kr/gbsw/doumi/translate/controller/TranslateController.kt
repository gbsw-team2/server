package hs.kr.gbsw.doumi.translate.controller

import hs.kr.gbsw.doumi.translate.dto.TextRequest
import hs.kr.gbsw.doumi.translate.dto.TextResponse
import hs.kr.gbsw.doumi.translate.dto.VoiceRequest
import hs.kr.gbsw.doumi.translate.dto.VoiceResponse
import hs.kr.gbsw.doumi.translate.service.TranslateService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/translate")
@RestController
class TranslateController(
    private val translateService: TranslateService
) {

    @PostMapping("/voice")
    fun uploadVoice(
        @RequestBody dto: VoiceRequest
    ): ResponseEntity<VoiceResponse> {
        val text = translateService.toText(dto.audio!!, dto.beforeLang!!) ?:
            return ResponseEntity(null, HttpStatus.BAD_REQUEST)

        val translated = translateService.translate(text, dto.beforeLang, dto.afterLang!!) ?:
            return ResponseEntity(null, HttpStatus.BAD_REQUEST)

        return ResponseEntity(VoiceResponse(translated), HttpStatus.OK)
    }

    @PostMapping("/text")
    fun uploadText(
        @RequestBody dto: TextRequest
    ): ResponseEntity<TextResponse> {
        val translated = translateService.translate(dto) ?:
            return ResponseEntity(null, HttpStatus.BAD_REQUEST)

        return ResponseEntity(TextResponse(translated), HttpStatus.OK)
    }

}