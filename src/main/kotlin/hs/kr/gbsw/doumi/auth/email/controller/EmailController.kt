package hs.kr.gbsw.doumi.auth.email.controller

import hs.kr.gbsw.doumi.auth.email.dto.EmailDto
import hs.kr.gbsw.doumi.auth.email.service.EmailService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/email")
class EmailController(
    private val emailService: EmailService
) {

    @PostMapping("/send")
    fun sendEmail(
        @RequestBody emailDto: EmailDto
    ): ResponseEntity<String> {
        return emailService.sendEmail(emailDto)
    }
}