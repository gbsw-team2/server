package hs.kr.gbsw.doumi.board.dto

import java.time.LocalDateTime

data class CreateCommentDto(
    val body: String,
)

data class UpdateCommentDto(
    val body: String
)

data class ResponseCommentDto(
    val id: Long,
    val body: String,
    val updatedAt: LocalDateTime,
    val isWritten: Boolean,
)