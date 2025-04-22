package hs.kr.gbsw.doumi.chatbot.controller

import hs.kr.gbsw.doumi.chatbot.dto.RecommendDto
import hs.kr.gbsw.doumi.chatbot.service.ChatbotService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/chatbot")
class ChatbotController(
    private val chatbotService: ChatbotService) {

    @GetMapping("/recommend")
    fun recommend(): ResponseEntity<List<RecommendDto>> {
        val recommendations = chatbotService.getRandomRecommends()
        return ResponseEntity.ok(recommendations)
    }
}