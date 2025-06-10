package hs.kr.gbsw.doumi.auth.user.controller

import hs.kr.gbsw.doumi.auth.user.dto.*
import hs.kr.gbsw.doumi.auth.user.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RequestMapping("/api/users")
@RestController
class UserController(
    private val userService: UserService
) {

    @PostMapping
    fun signup(
        @Valid @RequestBody dto: UserSignupRequest
    ): ResponseEntity<String> {
        return userService.signup(dto)
    }

    @PostMapping("/verify")
    fun verify(
        @Valid @RequestBody dto: UserSignupVerifyRequest
    ): ResponseEntity<String> {
        return userService.verify(dto)
    }

    @PostMapping("/login")
    fun login(
        @RequestBody @Valid dto: UserLoginRequest,
    ): ResponseEntity<Map<String, String>> {
        return userService.login(dto)
    }

    @GetMapping("/info")
    fun userInfo(principal: Principal): ResponseEntity<UserInfoResponse> {
        val email = principal.name
        val user = userService.userInfo(email)
        return ResponseEntity(user, HttpStatus.OK)
    }

    @PutMapping("/info")
    fun updateUserInfo(
        principal: Principal,
        @RequestBody dto: UserInfoRequest
    ): ResponseEntity<UserInfoResponse> {
        val email = principal.name
        val user = userService.updateUserInfo(dto, email)
        return ResponseEntity(user, HttpStatus.OK)
    }

    @PutMapping("/info/password")
    fun updateUserPassword(
        principal: Principal,
        @RequestBody password: String
    ): ResponseEntity<UserInfoResponse> {
        val email = principal.name
        val user = userService.updatePassword(password, email)
        return ResponseEntity(user, HttpStatus.OK)
    }

    @PostMapping("/refresh")
    fun refreshToken(
        @RequestParam email: String,
        @RequestHeader("Authorization") authHeader: String?
    ): ResponseEntity<Map<String, String>> {
        val accessToken = authHeader?.let {
            if (it.startsWith("Bearer ")) it.substring(7) else null
        } ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(mapOf("message" to "액세스 토큰이 필요합니다."))

        return userService.refreshToken(email, accessToken)
    }

    @PostMapping("/logout")
    fun logout(
        @RequestParam email: String
    ): ResponseEntity<String> {
        return userService.logout(email)
    }
}