package hs.kr.gbsw.doumi.board.repository

import hs.kr.gbsw.doumi.board.model.Post
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BoardRepository: JpaRepository<Post, Long> {
    fun findByCountryId(countryId: Int, spec: Specification<Post>, pageable: Pageable): Page<Post>
}