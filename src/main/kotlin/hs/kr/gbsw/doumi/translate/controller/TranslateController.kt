package hs.kr.gbsw.doumi.translate.controller

import hs.kr.gbsw.doumi.translate.dto.TextRequest
import hs.kr.gbsw.doumi.translate.dto.VoiceRequest
import hs.kr.gbsw.doumi.translate.service.TranslateService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RequestMapping("/api/translate")
@RestController
class TranslateController(
    private val translateService: TranslateService
) {

    @PostMapping("/voice")
    fun uploadVoice(
        @RequestPart audio: MultipartFile,
        @RequestParam beforeLang: String,
        @RequestParam afterLang: String
    ): ResponseEntity<String> {
        val text = translateService.toText(audio, beforeLang) ?: return ResponseEntity.badRequest().body("음성 변환 실패.")
        val translated = translateService.translate(text, beforeLang, afterLang) ?: return ResponseEntity.badRequest().body("번역 실패.")
        return ResponseEntity.ok().body(translated)
    }


    @PostMapping("/text")
    fun uploadText(
        @RequestBody dto: TextRequest
    ): ResponseEntity<String> {
        val translated = translateService.translate(dto) ?:
        return ResponseEntity.badRequest().body("음성 변환 실패.")

        return ResponseEntity.ok().body(translated)
    }

}