package hs.kr.gbsw.doumi.auth.user.service

import hs.kr.gbsw.doumi.auth.email.service.EmailService
import hs.kr.gbsw.doumi.auth.jwt.JwtTokenProvider
import hs.kr.gbsw.doumi.auth.user.dto.CountryDto
import hs.kr.gbsw.doumi.auth.user.dto.UserInfoResponse
import hs.kr.gbsw.doumi.auth.user.dto.UserLoginRequest
import hs.kr.gbsw.doumi.auth.user.dto.UserSignupRequest
import hs.kr.gbsw.doumi.auth.user.repository.UserRepository
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val emailService: EmailService
) {
    @Value("\${jwt.access_secret}")
    lateinit var access: String

    private val accessKey by lazy { Keys.hmacShaKeyFor(Decoders.BASE64.decode(access)) }

    fun signup(dto: UserSignupRequest): ResponseEntity<String> {
        val verified = emailService.validateEmailCode(dto.email, dto.vernum)
        if (verified.statusCode != HttpStatus.OK) {
            return verified
        }

        var user = userRepository.findByEmail(dto.email)
        if (user != null) {
            return ResponseEntity.status(400).body("이미 존재하는 이메일 입니다.")
        }
        user = dto.toEntity(dto.name, passwordEncoder.encode(dto.password))
        userRepository.save(user)

        return ResponseEntity.status(HttpStatus.OK).body("회원가입이 완료되었습니다.")
    }

    fun login(dto: UserLoginRequest): ResponseEntity<Map<String, String>> {
        val user = userRepository.findByEmail(dto.email)
            ?: return ResponseEntity.status(404).body(
                mapOf("message" to "존재하지 않는 이메일입니다.")
            )

        if (!passwordEncoder.matches(dto.password, user.password)) {
            return ResponseEntity.status(401).body(
                mapOf("message" to "비밀번호가 올바르지 않습니다.")
            )
        }

        val tokenInfo = jwtTokenProvider.createToken(user)
        return ResponseEntity.ok(
            mapOf(
                "message" to "로그인 성공",
                "accessToken" to tokenInfo.accessToken,
                "refreshToken" to tokenInfo.refreshToken
            )
        )
    }

    fun userInfo(email: String): UserInfoResponse {
        val user = userRepository.findByEmail(email)!!
        return UserInfoResponse(
            user.email,
            user.name,
            user.country!!,
            user.createdAt,
            user.provider
        )
    }

    fun updateCountry(accessToken: String, contryDto: CountryDto): ResponseEntity<String> {
        val claims = jwtTokenProvider.getClaims(accessToken, accessKey)
        val email = claims["email"] as String

        val user = userRepository.findByEmail(email)
            ?: throw IllegalArgumentException("사용자를 찾을 수 없습니다.")

        user.country = contryDto.contry
        userRepository.save(user)

        return ResponseEntity.status(HttpStatus.OK).body("회원가입이 완료되었습니다.")
    }
}