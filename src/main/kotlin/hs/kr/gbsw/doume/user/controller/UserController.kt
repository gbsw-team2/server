package hs.kr.gbsw.doume.user.controller

import hs.kr.gbsw.doume.common.dto.BaseResponse
import hs.kr.gbsw.doume.common.status.ResponseCode
import hs.kr.gbsw.doume.user.dto.UserSignupDto
import hs.kr.gbsw.doume.user.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
    fun signup(@RequestBody dto: UserSignupDto): ResponseEntity<String> {
        val result = userService.signup(dto)

        if (result.responseCode == ResponseCode.SUCCESS.name) {
            return ResponseEntity(result.message, HttpStatus.CREATED)
        }
        else {
            return ResponseEntity(result.message, HttpStatus.BAD_REQUEST)
        }
    }
}