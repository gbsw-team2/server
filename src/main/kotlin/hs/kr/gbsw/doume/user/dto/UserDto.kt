package hs.kr.gbsw.doume.user.dto

import hs.kr.gbsw.doume.user.model.Users
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class UserSignupDto(
    @field:Email(message = "유효한 이메일 형식을 사용해주세요.")
    @field:NotBlank(message = "이메일을 입력해 주세요.")
    val email: String,

    @field:NotBlank(message = "비밀번호를 입력해 주세요.")
    val password: String,

    var country: Int?
) {
    fun toEntity(password: String): Users =
        Users(email = email, password = password, country = country)
}

data class UserLoginDto(
    @field:Email(message = "유효한 이메일 형식을 사용해주세요.")
    @field:NotBlank(message = "이메일을 입력해 주세요.")
    val email: String,

    @field:NotBlank(message = "비밀번호를 입력해 주세요.")
    val password: String
)