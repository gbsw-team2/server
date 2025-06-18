package hs.kr.gbsw.doumi.chatbot.repository

import hs.kr.gbsw.doumi.chatbot.model.Recommend
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface RecommendRepository: JpaRepository<Recommend, Int> {

    fun findById(id: Int?): Recommend?

    @Query("SELECT r FROM Recommend r ORDER BY RAND() LIMIT 3")
    fun findRandomThree(): List<Recommend>
}