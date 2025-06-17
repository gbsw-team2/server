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

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(ex: NoSuchElementException): ResponseEntity<String> {
        return ResponseEntity.status(404).body("${ex.message}")
    }

    @ExceptionHandler(IllegalAccessException::class)
    fun handleIllegalAccess(ex: IllegalAccessException): ResponseEntity<String> {
        return ResponseEntity.status(401).body("${ex.message}")
    }

    @PostMapping
    fun signup(
        @Valid @RequestBody dto: UserSignupRequest
    ): ResponseEntity<String> {
        return userService.signup(dto)
    }

    @PostMapping("/login")
    fun login(
        @RequestBody @Valid dto: UserLoginRequest,
    ): ResponseEntity<Map<String, String>> {
        return userService.login(dto)
    }

    @GetMapping("/info")
    fun userInfo(principal: Principal?): ResponseEntity<UserInfoResponse> {
        if (principal == null) {
            throw IllegalAccessException()
        }
        val email = principal.name
        val result = userService.userInfo(email)
        return ResponseEntity(result, HttpStatus.OK)
    }

    @PutMapping("/info")
    fun updateUserInfo(
        principal: Principal?,
        @RequestBody dto: UserInfoRequest
    ): ResponseEntity<UserInfoResponse> {
        if (principal == null) {
            throw IllegalAccessException()
        }
        val email = principal.name
        val user = userService.updateUserInfo(dto, email)
        return ResponseEntity(user, HttpStatus.OK)
    }

    @PutMapping("/info/password")
    fun updateUserPassword(
        principal: Principal?,
        @RequestBody password: String
    ): ResponseEntity<UserInfoResponse> {
        if (principal == null) {
            throw IllegalAccessException()
        }
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