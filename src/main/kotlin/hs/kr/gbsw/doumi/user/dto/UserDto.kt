package hs.kr.gbsw.doumi.user.dto

import hs.kr.gbsw.doumi.user.model.Users
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

data class UserSignupRequest(
    @field:Email(message = "유효한 이메일 형식을 사용해주세요.")
    @field:NotBlank(message = "이메일을 입력해 주세요.")
    val email: String?,

    @field:Pattern(
        regexp = "(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}",
        message = "비밀번호는 영문, 숫자 및 특수문자(@$!%*?&#)를 포함하여 8자 이상으로 작성해주세요.")
    @field:NotBlank(message = "비밀번호를 입력해 주세요.")
    val password: String?,

    @field:NotNull(message = "국적을 선택해 주세요.")
    var country: Int?
) {
    fun toEntity(password: String): Users =
        Users(email = email!!, password = password, country = country)
}

data class UserLoginRequest(
    @field:Email(message = "유효한 이메일 형식을 사용해주세요.")
    @field:NotBlank(message = "이메일을 입력해 주세요.")
    val email: String,
    
    @field:NotBlank(message = "비밀번호를 입력해 주세요.")
    val password: String
)