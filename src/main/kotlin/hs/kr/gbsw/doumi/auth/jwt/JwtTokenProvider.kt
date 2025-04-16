package hs.kr.gbsw.doumi.auth.jwt

import hs.kr.gbsw.doumi.auth.user.model.Users
import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
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

    fun createToken(user: Users): TokenInfo {
        val now = Date()
        val accessExpiration = Date(now.time + ACCESS_EXPIRATION_MILLISECONDS)
        val refreshExpiration = Date(now.time + REFRESH_EXPIRATION_MILLISECONDS)

        val accessToken = Jwts.builder()
            .subject(user.email)
            .issuedAt(now)
            .expiration(accessExpiration)
            .claim("userId", user.id)
            .claim("email", user.email)
            .claim("auth", user.provider)
            .signWith(accessKey, Jwts.SIG.HS256)
            .compact()

        val refreshToken = Jwts.builder()
            .subject(user.email)
            .issuedAt(now)
            .expiration(refreshExpiration)
            .claim("userId", user.id)
            .claim("email", user.email)
            .signWith(refreshKey, Jwts.SIG.HS256)
            .compact()

        return TokenInfo("Bearer", accessToken, refreshToken)
    }

    fun validateToken(accessToken: String): Boolean {
        try {
            getClaims(accessToken, accessKey)
            return true
        } catch (e: Exception) {
            when (e) {
                is SecurityException -> {}
                is MalformedJwtException -> {}
                is ExpiredJwtException -> {}
                is UnsupportedJwtException -> {}
                is IllegalArgumentException -> {}
                else -> {}
            }
            println(e.message)
        }
        return false
    }

    fun validateRefreshToken(accessToken: String): Boolean {
        try {
            getClaims(accessToken, refreshKey)
            return true
        } catch (e: Exception) {
            when (e) {
                is SecurityException -> {}
                is MalformedJwtException -> {}
                is ExpiredJwtException -> {}
                is UnsupportedJwtException -> {}
                is IllegalArgumentException -> {}
                else -> {}
            }
            println(e.message)
        }
        return false
    }

    fun recreationAccessToken(refreshToken: String): String? {
        try {
            val claims = getClaims(refreshToken, refreshKey)
            val email = claims.subject
            val userId = claims["userId"] as Long
            val auth = claims["auth"] as String

            val now = Date()
            val accessExpiration = Date(now.time + ACCESS_EXPIRATION_MILLISECONDS)

            return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(accessExpiration)
                .claim("userId", userId)
                .claim("email", email)
                .claim("auth", auth)
                .signWith(accessKey, Jwts.SIG.HS256)
                .compact()
        } catch (e: Exception) {
            println(e.message)
            return null
        }
    }

    fun getAuthentication(newAccessToken: String): UsernamePasswordAuthenticationToken? {
        val claims = getClaims(newAccessToken, accessKey)
        val email = claims.subject
        val auth = claims["auth"] as String
        val authorities = auth.split(",").map { SimpleGrantedAuthority(it.trim()) }
        val principal = org.springframework.security.core.userdetails.User(email, "", authorities)
        return UsernamePasswordAuthenticationToken(principal, "", authorities)
    }


    fun getClaims(accessToken: String, key: SecretKey): Claims =
        Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(accessToken)
            .payload

}
