package hs.kr.gbsw.doumi.board.repository

import hs.kr.gbsw.doumi.board.model.Like
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LikeRepository: JpaRepository<Like, Long> {
    fun getLikeByUserEmailAndPostId(email: String, postId: Long): Like?
    fun existsByUserEmailAndId(email: String, postId: Long): Boolean
    fun getLikesByPostId(postId: Long): Int
}