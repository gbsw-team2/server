package hs.kr.gbsw.doumi.board.repository

import hs.kr.gbsw.doumi.board.model.Comment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository: JpaRepository<Comment, Long> {
    fun getCommentsByPostId(postId: Long): List<Comment>
}