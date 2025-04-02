package hs.kr.gbsw.doumi.auth.jwt

data class TokenInfo(
    val grantType: String,
    val accessToken: String,
    val refreshToken: String,
)
