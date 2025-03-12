package hs.kr.gbsw.doume.user.dto

import hs.kr.gbsw.doume.user.model.Users

data class UserSignupDto(
    val email: String,
    val password: String,
    var country: Int?
) {
    fun toEntity(): Users =
        Users(email = email, password = password, country = country)
}

data class UserLoginDto(
    val username: String,
    val password: String
)