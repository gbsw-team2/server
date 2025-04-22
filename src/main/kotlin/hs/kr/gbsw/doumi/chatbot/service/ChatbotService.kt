package hs.kr.gbsw.doumi.chatbot.service

import hs.kr.gbsw.doumi.chatbot.dto.RecommendDto
import hs.kr.gbsw.doumi.chatbot.repository.RecommendRepository
import org.springframework.stereotype.Service

@Service
class ChatbotService(
    private val recommendRepository: RecommendRepository
) {

    fun getRandomRecommends(): List<RecommendDto> {

        val recommendations = recommendRepository.findRandomThree()
        return recommendations.map {
            RecommendDto(question = it.question)
        }
    }
}