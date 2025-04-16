package hs.kr.gbsw.doumi.auth.user.controller

import hs.kr.gbsw.doumi.auth.jwt.dto.CustomUser
import hs.kr.gbsw.doumi.auth.user.dto.CountryDto
import hs.kr.gbsw.doumi.auth.user.dto.UserInfoResponse
import hs.kr.gbsw.doumi.auth.user.dto.UserLoginRequest
import hs.kr.gbsw.doumi.auth.user.dto.UserSignupRequest
import hs.kr.gbsw.doumi.auth.user.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/users")
@RestController
class UserController(
    private val userService: UserService
) {

    @PostMapping
    fun signup(@Valid @RequestBody dto: UserSignupRequest): ResponseEntity<String> {
        return userService.signup(dto)
    }

    @PostMapping("/login")
    fun login(@RequestBody @Valid dto: UserLoginRequest): ResponseEntity<Map<String, String>> {
        return userService.login(dto)
    }

    @GetMapping("/info")
    fun userInfo(authentication: Authentication): ResponseEntity<UserInfoResponse> {
        val email = (SecurityContextHolder.getContext().authentication.principal as CustomUser).username
        val user = userService.userInfo(email)
        return ResponseEntity(user, HttpStatus.OK)
    }

    @PostMapping("/update-country")
    fun updateCountry(
        @RequestHeader("Authorization") authorizationHeader: String,
        @RequestBody countryDto: CountryDto
    ): ResponseEntity<ResponseEntity<String>> {
        val accessToken = authorizationHeader.substringAfter("Bearer ")

        return ResponseEntity.ok(userService.updateCountry(accessToken, countryDto))
    }

}