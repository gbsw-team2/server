package hs.kr.gbsw.doumi.board.dto

import hs.kr.gbsw.doumi.auth.user.model.Country
import hs.kr.gbsw.doumi.auth.user.model.Users
import hs.kr.gbsw.doumi.board.model.Post
import java.time.LocalDateTime

data class CreatePostDto(
    val title: String,
    val body: String,
    val country: Int,
) {
    fun toEntity(user: Users, country: Country): Post {
        return Post(
            title = this.title,
            body = this.body,
            country = country,
            user = user,
            updatedAt = LocalDateTime.now(),
            isWritten = false
        )
    }
}

data class ResponsePostDto(
    val title: String,
    val body: String,
    val like: Int,
    val isLike: Boolean,
    val view: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val isWritten: Boolean,
)