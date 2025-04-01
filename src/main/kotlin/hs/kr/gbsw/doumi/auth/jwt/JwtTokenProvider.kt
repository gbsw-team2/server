package hs.kr.gbsw.doumi.auth.jwt

import hs.kr.gbsw.doumi.auth.jwt.dto.CustomUser
import hs.kr.gbsw.doumi.user.model.Users
import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

const val ACCESS_EXPIRATION_MILLISECONDS: Long = 1000 * 60 * 30
const val REFRESH_EXPIRATION_MILLISECONDS: Long = 1000 * 60 * 60 * 24 * 14

@Component
class JwtTokenProvider {

    @Value("\${jwt.access_secret}")
    lateinit var access: String

    @Value("\${jwt.refresh_secret}")
    lateinit var refresh: String

    private val accessKey by lazy { Keys.hmacShaKeyFor(Decoders.BASE64.decode(access)) }
    private val refreshKey by lazy { Keys.hmacShaKeyFor(Decoders.BASE64.decode(refresh)) }

    fun createToken(authentication: Authentication): TokenInfo {
        val auth = authentication.authorities
            .joinToString(",", transform = GrantedAuthority::getAuthority)

        val now = Date()
        val accessExpiration = Date(now.time + ACCESS_EXPIRATION_MILLISECONDS)
        val refreshExpiration = Date(now.time + REFRESH_EXPIRATION_MILLISECONDS)

        val user = authentication.principal as CustomUser

        val accessToken = Jwts.builder()
            .subject(user.username)
            .issuedAt(now)
            .expiration(accessExpiration)
            .claim("auth", auth)
            .claim("userId", user.userId)
            .signWith(accessKey, Jwts.SIG.HS256)
            .compact()

        val refreshToken = Jwts.builder()
            .subject(user.username)
            .issuedAt(now)
            .expiration(refreshExpiration)
            .claim("auth", auth)
            .claim("userId", user.userId)
            .signWith(refreshKey, Jwts.SIG.HS256)
            .compact()

        return TokenInfo("Bearer", accessToken, refreshToken)
    }

    fun getAuthentication(token: String): Authentication {
        val claims: Claims = getClaims(token, accessKey)

        val auth = claims["auth"] ?: throw RuntimeException("잘못된 토큰입니다.")
        val userId = claims["userId"] ?: throw RuntimeException("잘못된 토큰입니다.")

        val authorities: Collection<GrantedAuthority> =
            (auth as String).split(",")
                .map { SimpleGrantedAuthority(it) }

        val principal: UserDetails = CustomUser(userId.toString().toLong(), claims.subject, "", authorities)

        return UsernamePasswordAuthenticationToken(principal, "", authorities)
    }

    fun validateToken(token: String): Boolean {
        try {
            getClaims(token, accessKey)
            return true
        } catch (e: Exception) {
            when (e) {
                is SecurityException -> {}          // 유효하지 않은 토큰
                is MalformedJwtException -> {}      // 유효하지 않은 토큰
                is ExpiredJwtException -> {}        // 만료된 토큰
                is UnsupportedJwtException -> {}    // 지원되지 않는 토큰
                is IllegalArgumentException -> {}   // claims 문자열 비어있음
                else -> {}
            }
            println(e.message)
        }
        return false
    }

    fun validateRefreshToken(token: String): Boolean {
        try {
            getClaims(token, refreshKey)
            return true
        } catch (e: Exception) {
            when (e) {
                is SecurityException -> {}          // 유효하지 않은 토큰
                is MalformedJwtException -> {}      // 유효하지 않은 토큰
                is ExpiredJwtException -> {}        // 만료된 토큰
                is UnsupportedJwtException -> {}    // 지원되지 않는 토큰
                is IllegalArgumentException -> {}   // claims 문자열 비어있음
                else -> {}
            }
            println(e.message)
        }
        return false
    }

    fun recreationAccessToken(refreshToken: String): String? {
        try {
            val claims = getClaims(refreshToken, refreshKey)
            val username = claims.subject
            val auth = claims["auth"] as String
            val userId = claims["userId"] as String

            val now = Date()
            val accessExpiration = Date(now.time + ACCESS_EXPIRATION_MILLISECONDS)

            return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(accessExpiration)
                .claim("auth", auth)
                .claim("userId", userId)
                .signWith(accessKey, Jwts.SIG.HS256)
                .compact()
        } catch (e: Exception) {
            println(e.message)
            return null
        }
    }

    private fun getClaims(token: String, key: SecretKey): Claims =
        Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload

}