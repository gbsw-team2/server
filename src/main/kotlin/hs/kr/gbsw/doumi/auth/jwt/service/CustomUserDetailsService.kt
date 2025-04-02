package hs.kr.gbsw.doumi.auth.jwt.service

import hs.kr.gbsw.doumi.auth.jwt.dto.CustomUser
import hs.kr.gbsw.doumi.user.model.Users
import hs.kr.gbsw.doumi.user.repository.UserRepository
import jakarta.persistence.EnumType
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository,
): UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails =
        userRepository.findByEmail(username)?.let {
            createUserDetails(it)
        } ?: throw UsernameNotFoundException("해당 유저는 존재하지 않습니다.")

    private fun createUserDetails(user: Users): UserDetails =
        CustomUser(
            user.id!!,
            user.email,
            user.password,
            listOf(SimpleGrantedAuthority("USER"))
        )

}