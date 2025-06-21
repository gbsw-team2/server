package hs.kr.gbsw.doumi.auth.user.service

import hs.kr.gbsw.doumi.auth.jwt.ACCESS_EXPIRATION_MILLISECONDS
import hs.kr.gbsw.doumi.auth.jwt.JwtTokenProvider
import hs.kr.gbsw.doumi.auth.redis.service.RedisService
import hs.kr.gbsw.doumi.auth.user.dto.*
import hs.kr.gbsw.doumi.auth.user.repository.CountryRepository
import hs.kr.gbsw.doumi.auth.user.repository.UserRepository
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val countryRepository: CountryRepository,
    private val redisService: RedisService
) {
    @Value("\${jwt.access_secret}")
    lateinit var access: String

//    private val accessKey by lazy { Keys.hmacShaKeyFor(Decoders.BASE64.decode(access)) }

    fun signup(dto: UserSignupRequest): ResponseEntity<String> {
        val verified = redisService.getVerifyEmail(dto.email!!)
        if (verified != "verified") {
            return ResponseEntity.status(401).body("인증되지 않은 이메일입니다.")
        }

        var user = userRepository.findByEmail(dto.email)
        if (user != null) {
            return ResponseEntity.status(400).body("이미 존재하는 이메일 입니다.")
        }

        val country = countryRepository.findById(dto.country).get()
        
        user = dto.toEntity(dto.name!!, passwordEncoder.encode(dto.password), country, dto.contact)

        userRepository.save(user)

        return ResponseEntity.status(HttpStatus.OK).body("회원가입이 완료되었습니다.")
    }

    fun login(dto: UserLoginRequest): ResponseEntity<UserLoginResponse> {
        val user = userRepository.findByEmail(dto.email)
            ?: return ResponseEntity.status(404).body(
                UserLoginResponse("존재하지 않는 이메일입니다.", null)
            )

        if (!passwordEncoder.matches(dto.password, user.password)) {
            return ResponseEntity.status(401).body(
                UserLoginResponse("비밀번호가 올바르지 않습니다.", null)
            )
        }

        val tokenInfo = jwtTokenProvider.createToken(user)

//        val cookie = ResponseCookie.from("access_token", tokenInfo.accessToken)
//            .httpOnly(true)
//            .secure(false) //현재는 개발을 위해 Https off
//            .path("/")
//            .maxAge(ACCESS_EXPIRATION_MILLISECONDS / 1000)
//            .sameSite("Lax")
//            .build()
//
//        val headers = HttpHeaders().apply {
//            add(HttpHeaders.SET_COOKIE, cookie.toString())
//        }

        val response = UserLoginResponse("로그인 성공", tokenInfo.accessToken)

        return ResponseEntity.ok().body(response)
    }

    fun userInfo(email: String): UserInfoResponse {
        val user = userRepository.findByEmail(email)
            ?: throw NoSuchElementException()
        return UserInfoResponse(
            user.email,
            user.name,
            user.country!!.id,
            user.contact,
            user.createdAt
        )
    }

    fun updateUserInfo(dto: UserInfoRequest, email: String): UserInfoResponse {
        val user = userRepository.findByEmail(email)!!

        if (user.name != dto.name) {
            user.name = dto.name
        }
        if (user.country!!.id != dto.countryId) {
            val country = countryRepository.findById(dto.countryId).get()
            user.country = country
        }
        if (user.contact != dto.contact) {
            user.contact = dto.contact
        }

        userRepository.save(user)

        return UserInfoResponse(
            user.email,
            user.name,
            user.country!!.id,
            user.contact,
            user.createdAt
        )
    }

    fun updatePassword(password: String, email: String): UserInfoResponse {
        val user = userRepository.findByEmail(email)!!

        if (!passwordEncoder.matches(password, user.password)) {
            user.password = passwordEncoder.encode(password)
        }

        userRepository.save(user)

        return UserInfoResponse(
            user.email,
            user.name,
            user.country!!.id,
            user.contact,
            user.createdAt
        )
    }

    fun refreshToken(email: String, accessToken: String): ResponseEntity<UserLoginResponse> {
        if (!jwtTokenProvider.validateExpiredAccessToken(accessToken, email)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                .body(mapOf("message" to "유효하지 않은 액세스 토큰입니다."))
                .body(UserLoginResponse("유효하지 않은 액세스 토큰입니다.", null))
        }

        if (!jwtTokenProvider.validateRefreshToken(email)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(UserLoginResponse("유효하지 않은 리프레시 토큰입니다.", null))

        }

        val newAccessToken = jwtTokenProvider.recreationAccessToken(email)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(UserLoginResponse("액세스 토큰 재발급에 실패했습니다.", null))


//        val cookie = ResponseCookie.from("doumi_access_token", newAccessToken)
//            .httpOnly(true)
//            .secure(false) //현재는 개발을 위해 Https off
//            .path("/")
//            .maxAge(ACCESS_EXPIRATION_MILLISECONDS / 1000)
//            .sameSite("Lax")
//            .build()
//
//        val headers = HttpHeaders().apply {
//            add(HttpHeaders.SET_COOKIE, cookie.toString())
//        }
//
//        return ResponseEntity.ok()
//            .headers(headers)
//            .body(mapOf("message" to "토큰 갱신 성공"))
        val response = UserLoginResponse("토큰 갱신 성공", newAccessToken)

        return ResponseEntity.ok().body(response)
    }

    fun logout(email: String): ResponseEntity<String> {
        redisService.deleteRefreshToken(email)

        return ResponseEntity.ok("로그아웃 성공")
    }
}