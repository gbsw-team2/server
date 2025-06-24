package hs.kr.gbsw.doumi.board.repository

import hs.kr.gbsw.doumi.board.model.Post
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface BoardRepository: JpaRepository<Post, Long>, JpaSpecificationExecutor<Post>