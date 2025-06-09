package hs.kr.gbsw.doumi.auth.redis.service

import hs.kr.gbsw.doumi.auth.jwt.REFRESH_EXPIRATION_MILLISECONDS
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RedisService(private val redisTemplate: RedisTemplate<String, Any>) {
    @Value("\${jwt.refresh_secret}")
    lateinit var refresh: String

    fun saveRefreshToken(username: String, refreshToken: String) {
        redisTemplate.opsForValue().set(
            refresh + username,
            refreshToken,
            REFRESH_EXPIRATION_MILLISECONDS,
            TimeUnit.SECONDS
        )
    }

    fun getRefreshToken(username: String?): String? {
        return redisTemplate.opsForValue().get(refresh + username) as String?
    }

    fun deleteRefreshToken(username: String?) {
        redisTemplate.delete(refresh + username)
    }

    fun saveVerifyEmail(email: String) {
        redisTemplate.opsForValue().set(
            "verify:email:$email",
            "verified",
            600L, // 10분
            TimeUnit.SECONDS
        )
    }

    fun getVerifyEmail(email: String): String? {
        return redisTemplate.opsForValue().get("verify:email:$email") as String?
    }
}