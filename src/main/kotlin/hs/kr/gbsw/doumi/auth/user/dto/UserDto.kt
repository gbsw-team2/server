package hs.kr.gbsw.doumi.auth.user.dto

import hs.kr.gbsw.doumi.auth.user.model.Country
import hs.kr.gbsw.doumi.auth.user.model.Users
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import java.time.LocalDateTime

data class UserSignupRequest(
    @field:Email(message = "유효한 이메일 형식을 사용해주세요.")
    @field:NotBlank(message = "이메일을 입력해 주세요.")
    val email: String,

    @field:NotBlank(message = "이름을 입력해 주세요.")
    val name: String,

    @field:Pattern(
        regexp = "(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}",
        message = "비밀번호는 영문, 숫자 및 특수문자(@$!%*?&#)를 포함하여 8자 이상으로 작성해주세요."
    )
    @field:NotBlank(message = "비밀번호를 입력해 주세요.")
    val password: String,

    @field:NotNull(message = "국적을 선택해 주세요.")
    var country: Int,

    @field:Pattern(
        regexp = "\\d{2,3}-\\d{3,4}-\\d{4}",
        message = "전화번호를 형식에 맞추어 작성해주세요."
    )
    var contact: String
) {
  fun toEntity(name: String, password: String, country: Country, contact: String, provider: String? = null, providerId: String? = null): Users =
        Users(
            email = email,
            name = name,
            password = password,
            country = country,
            contact = contact,
            provider = provider ?: "default",
            providerId = providerId,
        )
}

data class UserSignupVerifyRequest(
    @field:Email(message = "유효한 이메일 형식을 사용해주세요.")
    @field:NotBlank(message = "이메일을 입력해 주세요.")
    val email: String,

    @field:NotBlank(message = "인증번호를 입력해 주세요.")
    val vernum: String,
)

data class UserLoginRequest(
    @field:Email(message = "유효한 이메일 형식을 사용해주세요.")
    @field:NotBlank(message = "이메일을 입력해 주세요.")
    val email: String,

    @field:NotBlank(message = "비밀번호를 입력해 주세요.")
    val password: String
)

data class UserInfoResponse(
    val email: String,
    val name: String?,
    val country: Int?,
    val contact: String?,
    val createdAt: LocalDateTime,
)

data class UserInfoRequest(
    val name: String,
    val countryId: Int,
    val contact: String?,
)