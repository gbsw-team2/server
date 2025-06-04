package hs.kr.gbsw.doumi.auth.user.service

import hs.kr.gbsw.doumi.auth.email.service.EmailService
import hs.kr.gbsw.doumi.auth.jwt.JwtTokenProvider
import hs.kr.gbsw.doumi.auth.jwt.TokenInfo
import hs.kr.gbsw.doumi.auth.user.dto.UserInfoResponse
import hs.kr.gbsw.doumi.auth.user.dto.UserLoginRequest
import hs.kr.gbsw.doumi.auth.user.dto.UserSignupRequest
import hs.kr.gbsw.doumi.auth.user.model.Country
import hs.kr.gbsw.doumi.auth.user.repository.CountryRepository
import hs.kr.gbsw.doumi.auth.user.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val emailService: EmailService,
    private val countryRepository: CountryRepository
) {

    fun signup(dto: UserSignupRequest): ResponseEntity<String> {
        val verified = emailService.validateEmailCode(dto.email!!, dto.vernum!!)
        if (verified.statusCode != HttpStatus.OK) {
            return verified
        }

        var user = userRepository.findByEmail(dto.email)
        if (user != null) {
            return ResponseEntity("이미 존재하는 이메일입니다.", HttpStatus.BAD_REQUEST)
        }

        val country = countryRepository.findById(dto.country!!).get()

        user = dto.toEntity(passwordEncoder.encode(dto.password), country)
        userRepository.save(user)

        return ResponseEntity("회원가입이 완료 되었습니다.", HttpStatus.CREATED)
    }

    fun login(dto: UserLoginRequest): TokenInfo {
        val authenticationToken = UsernamePasswordAuthenticationToken(dto.email, dto.password)
        val authentication = authenticationManagerBuilder.`object`.authenticate(authenticationToken)

        return jwtTokenProvider.createToken(authentication)
    }

    fun userInfo(email: String): UserInfoResponse {
        val user = userRepository.findByEmail(email)!!
        return UserInfoResponse(
            user.email,
            user.country!!,
            user.createdAt
        )
    }

}