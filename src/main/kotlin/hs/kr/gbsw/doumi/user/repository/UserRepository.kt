package hs.kr.gbsw.doumi.user.repository

import hs.kr.gbsw.doumi.user.model.Users
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<Users, Long> {
    fun findByEmail(email: String): Users?
}