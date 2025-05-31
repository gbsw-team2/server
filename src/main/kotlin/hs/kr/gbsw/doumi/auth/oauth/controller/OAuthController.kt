package hs.kr.gbsw.doumi.auth.oauth.controller

import hs.kr.gbsw.doumi.auth.oauth.service.GoogleOAuthService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/login/oauth2")
class OAuthController(
    private val googleOAuthService: GoogleOAuthService
) {

    @GetMapping("/google")
    fun googleOAuth(response: HttpServletResponse) {
        val loginUrl = googleOAuthService.getGoogleLoginUrl()

        response.sendRedirect(loginUrl)
    }

    @GetMapping("/code/google")
    fun handleGoogleCallback(@RequestParam("code") authCode: String): ResponseEntity<ResponseEntity<Map<String, Any>>> {
        val googleToken = googleOAuthService.exchangeAuthCodeForTokens(authCode)
        val response = googleOAuthService.handleOAuthUser(googleToken)

        return ResponseEntity.ok(response)
    }

}
