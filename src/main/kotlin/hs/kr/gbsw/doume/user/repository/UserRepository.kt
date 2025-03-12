package hs.kr.gbsw.doume.user.repository

import hs.kr.gbsw.doume.user.model.Users
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<Users, Long> {
    fun findByEmail(email: String): Users?
}