package hs.kr.gbsw.doumi.board.dto

import hs.kr.gbsw.doumi.auth.user.model.Country
import hs.kr.gbsw.doumi.auth.user.model.Users
import hs.kr.gbsw.doumi.board.model.Post
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

data class CreatePostDto(
    val titie: String,
    val body: String,
    val country: Int,
) {
    fun toEntity(user: Users, country: Country): Post {
        return Post(
            title = this.titie,
            body = this.body,
            country = country,
            user = user,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            isWritten = false
        )
    }
}

data class ModifyPostDto(
    val id: Long,
    val title: String,
    val body: String,
    val country: Int,
)