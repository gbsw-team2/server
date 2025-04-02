package hs.kr.gbsw.doumi.user.controller

import hs.kr.gbsw.doumi.auth.jwt.TokenInfo
import hs.kr.gbsw.doumi.auth.jwt.dto.CustomUser
import hs.kr.gbsw.doumi.common.status.ResponseCode
import hs.kr.gbsw.doumi.user.dto.UserInfoResponse
import hs.kr.gbsw.doumi.user.dto.UserLoginRequest
import hs.kr.gbsw.doumi.user.dto.UserSignupRequest
import hs.kr.gbsw.doumi.user.model.Users
import hs.kr.gbsw.doumi.user.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
    fun login(@RequestBody @Valid dto: UserLoginRequest): ResponseEntity<TokenInfo> {
        val tokenInfo = userService.login(dto)
        return ResponseEntity(tokenInfo, HttpStatus.OK)
    }

    @GetMapping("/info")
    fun userInfo(authentication: Authentication): ResponseEntity<UserInfoResponse> {
        val email = (SecurityContextHolder.getContext().authentication.principal as CustomUser).username
        val user = userService.userInfo(email)
        return ResponseEntity(user, HttpStatus.OK)
    }

}