package hs.kr.gbsw.doumi.auth.email.service

import hs.kr.gbsw.doumi.auth.email.dto.EmailDto
import org.springframework.core.io.ClassPathResource
import org.springframework.http.ResponseEntity
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.security.SecureRandom
import java.time.LocalDateTime

@Service
class EmailService(
    private val mailSender: JavaMailSender
) {
    private val verificationMap: MutableMap<String, Pair<String, LocalDateTime>> = mutableMapOf()

    fun createVerifyCode(): String {
        val secureRandom = SecureRandom()
        val code = secureRandom.nextInt(900000) + 100000
        return code.toString()
    }

    fun sendEmail(emailDto: EmailDto): ResponseEntity<String> {
        return try {
            val verifyCode = createVerifyCode()

            val verifyCodeTemplateResource = ClassPathResource("templates/verifyCode.html")
            val verifyCodeHtmlContent = verifyCodeTemplateResource.inputStream
                .bufferedReader(StandardCharsets.UTF_8)
                .use { it.readText() }
            val htmlContent = verifyCodeHtmlContent.replace("{AUTH_CODE}", verifyCode)

            val message = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true, "UTF-8")

            helper.setTo(emailDto.email)
            helper.setSubject("[도우미] 인증 코드")
            helper.setText(htmlContent, true)

            mailSender.send(message)

            val expiryTime = LocalDateTime.now().plusMinutes(10)
            verificationMap[emailDto.email] = Pair(verifyCode, expiryTime)

            ResponseEntity.ok("인증번호 전송에 성공했습니다.")
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.status(500).body("인증번호 전송에 실패했습니다.")
        }
    }

    fun validateEmailCode(userEmail: String, verifyCode: String): ResponseEntity<String> {
        return try {
            val verificationInfo = verificationMap[userEmail]
                ?: return ResponseEntity.status(404).body("이 이메일에 대한 인증 코드를 찾을 수 없습니다.")

            val storedCode = verificationInfo.first
            val expiryTime = verificationInfo.second

            if (LocalDateTime.now().isAfter(expiryTime)) {
                verificationMap.remove(userEmail)
                return ResponseEntity.status(410).body("인증코드가 만료되었습니다.")
            }

            if (storedCode != verifyCode) {
                return ResponseEntity.status(401).body("인증코드가 일치하지 않습니다.")
            }

            verificationMap.remove(userEmail)
            ResponseEntity.ok("인증에 성공했습니다.")
        } catch (e: Exception) {
            ResponseEntity.status(500).body("인증 중에 오류가 발생했습니다.")
        }
    }
}