package hs.kr.gbsw.doumi.auth.email.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class EmailDto(
    val email: String
)

data class EmailVerifyRequest(
    @field:Email(message = "유효한 이메일 형식을 사용해주세요.")
    @field:NotBlank(message = "이메일을 입력해 주세요.")
    val email: String,

    @field:NotBlank(message = "인증번호를 입력해 주세요.")
    val vernum: String,
)