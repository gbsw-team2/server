package hs.kr.gbsw.doumi.auth.user.repository

import hs.kr.gbsw.doumi.auth.user.model.Users
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<Users, Long> {
    fun findByEmail(email: String): Users?
}