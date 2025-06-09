package hs.kr.gbsw.doumi.auth.oauth.service

import hs.kr.gbsw.doumi.auth.jwt.ACCESS_EXPIRATION_MILLISECONDS
import hs.kr.gbsw.doumi.auth.jwt.JwtTokenProvider
import hs.kr.gbsw.doumi.auth.user.model.Users
import hs.kr.gbsw.doumi.auth.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime

@Service
class GoogleOAuthService(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userRepository: UserRepository,
) {

    @Value("\${spring.security.oauth2.client.registration.google.client-id}")
    lateinit var clientId: String

    @Value("\${spring.security.oauth2.client.registration.google.client-secret}")
    lateinit var clientSecret: String

    @Value("\${spring.security.oauth2.client.registration.google.redirect-uri}")
    lateinit var redirectUri: String

    fun getGoogleLoginUrl(): String {
        return "https://accounts.google.com/o/oauth2/v2/auth" +
                "?client_id=$clientId" +
                "&redirect_uri=$redirectUri" +
                "&response_type=code" +
                "&scope=email profile"
    }

    fun exchangeAuthCodeForTokens(authCode: String): Map<String, Any> {
        val tokenUrl = "https://oauth2.googleapis.com/token"

        val requestBody = LinkedMultiValueMap<String, String>().apply {
            add("code", authCode)
            add("client_id", clientId)
            add("client_secret", clientSecret)
            add("redirect_uri", redirectUri)
            add("grant_type", "authorization_code")
        }

        val restTemplate = RestTemplate()
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
        }
        val entity = HttpEntity(requestBody, headers)

        return try {
            val response = restTemplate.postForEntity(tokenUrl, entity, Map::class.java)

            if (response.statusCode == HttpStatus.OK) {
                @Suppress("UNCHECKED_CAST")
                response.body as? Map<String, Any>
                    ?: throw RuntimeException("응답 형식이 올바르지 않습니다.")
            } else {
                throw RuntimeException("Google 토큰 요청 실패")
            }
        } catch (_: Exception) {
            throw RuntimeException("Google 인증 코드를 처리하는 중 오류 발생")
        }
    }


    fun handleOAuthUser(token: Map<String, Any>): ResponseEntity<Map<String, Any>> {
        val accessToken = token["access_token"] as String

        val userInfo = fetchGoogleUserInfo(accessToken)

        val email = userInfo["email"] as String
        val name = userInfo["given_name"] as String
        val providerId = userInfo["sub"] as String
        val provider = "google"

        val user = userRepository.findByEmail(email)
            ?: userRepository.save(
                Users(
                    email = email,
                    name = name,
                    password = null,
                    country = null,
                    createdAt = LocalDateTime.now(),
                    provider = provider,
                    providerId = providerId
                )
            )

        val tokenInfo = jwtTokenProvider.createToken(user)

        val cookie = ResponseCookie.from("access_token", tokenInfo.accessToken)
            .httpOnly(true)
            .secure(false) //현재는 개발을 위해 Https off
            .path("/")
            .maxAge(ACCESS_EXPIRATION_MILLISECONDS / 1000)
            .sameSite("Lax")
            .build()

        val headers = HttpHeaders().apply {
            add(HttpHeaders.SET_COOKIE, cookie.toString())
        }

        return ResponseEntity.ok()
            .headers(headers)
            .body(mapOf(
                "message" to "토큰 갱신 성공",
                "newUser" to (user.country == null)
            ))
    }

    private fun fetchGoogleUserInfo(accessToken: String): Map<String, Any> {
        val userInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo"
        val headers = HttpHeaders().apply {
            set("Authorization", "Bearer $accessToken")
        }
        val entity = HttpEntity<String>(headers)
        val restTemplate = RestTemplate()
        val response = restTemplate.exchange(
            userInfoUrl,
            HttpMethod.GET,
            entity,
            Map::class.java
        )

        @Suppress("UNCHECKED_CAST")
        return response.body as Map<String, Any>
    }


}
