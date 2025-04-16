package hs.kr.gbsw.doumi.auth.jwt.service

import hs.kr.gbsw.doumi.auth.jwt.dto.CustomUser
import hs.kr.gbsw.doumi.auth.user.model.Users
import hs.kr.gbsw.doumi.auth.user.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository,
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails =
        userRepository.findByEmail(username)?.let {
            createUserDetails(it)
        } ?: throw UsernameNotFoundException("해당 유저는 존재하지 않습니다.")

    private fun createUserDetails(user: Users): UserDetails {
        val authorities = mutableListOf<SimpleGrantedAuthority>()

        // 사용자 권한을 설정. 예를 들어 OAuth 사용자의 경우 적절한 권한 부여.
        authorities.add(SimpleGrantedAuthority("ROLE_USER"))
        if (user.provider != null) {
            authorities.add(SimpleGrantedAuthority("ROLE_OAUTH"))
        }

        return CustomUser(
            user.id!!,
            user.email,
            user.password?: "",
            authorities
        )
    }
}