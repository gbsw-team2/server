package hs.kr.gbsw.doumi.board.service

import hs.kr.gbsw.doumi.board.dto.CreateCommentDto
import hs.kr.gbsw.doumi.auth.user.repository.UserRepository
import hs.kr.gbsw.doumi.board.dto.ResponseCommentDto
import hs.kr.gbsw.doumi.board.model.Comment
import hs.kr.gbsw.doumi.board.repository.CommentRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CommentService(
    val commentRepository: CommentRepository,
    val userRepository: UserRepository,
    val boardService: BoardService,
) {

    fun create(
        dto: CreateCommentDto,
        email: String,
        postId: Long
    ): Comment {
        val user = userRepository.findByEmail(email)!!
        val post = boardService.getPost(postId)
        val comment = Comment(body = dto.body, user = user, post = post)
        return commentRepository.save(comment)
    }

    fun getCommentByPost(postId: Long): List<ResponseCommentDto> {
        val comments: List<Comment> = commentRepository.getCommentsByPostId(postId)
        if (comments.isEmpty()) throw NoSuchElementException("Commens by postId $postId not found.")

        return comments.map { comment ->
            ResponseCommentDto(
                id = comment.id!!,
                body = comment.body,
                createdAt = comment.createdAt,
                updatedAt = comment.updatedAt,
                isWritten = comment.isWritten,
            )
        }
    }

    fun getComment(id: Long): Comment {
        return commentRepository.findById(id).orElseThrow { NoSuchElementException("Comment with id $id not found.") }
    }

    fun updateComment(
        commentId: Long,
        dto: CreateCommentDto,
        email: String
    ): Pair<Comment, Boolean> {
        val comment = getComment(commentId)

        if (comment.user.email != email) {
            throw IllegalAccessException("Can only modify own comment.")
        }

        var modified = false;

        if (comment.body != dto.body) {
            comment.body = dto.body
            comment.updatedAt = LocalDateTime.now()
            comment.isWritten = true
            modified = true
        }

        return if (modified) Pair(commentRepository.save(comment), modified) else Pair(comment, modified)
    }

    fun deleteComment(commentId: Long, email: String) {
        val comment = getComment(commentId)

        if (comment.user.email != email) {
            throw IllegalAccessException("Can only delete own comment.")
        }

        commentRepository.delete(comment)
    }

}