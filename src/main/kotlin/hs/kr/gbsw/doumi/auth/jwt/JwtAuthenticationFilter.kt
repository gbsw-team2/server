package hs.kr.gbsw.doumi.auth.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.GenericFilterBean

class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider
): GenericFilterBean() {

    override fun doFilter(
        request: ServletRequest?,
        response: ServletResponse?,
        chain: FilterChain?
    ) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse

        if (httpRequest.requestURI == "/api/users/login") {
            chain?.doFilter(request, response)
            return
        }

        val accessToken = resolveToken(httpRequest)

        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            val authentication = jwtTokenProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().authentication = authentication
        } else {
            val refreshToken = resolveRefreshToken(httpRequest)
            if (refreshToken != null && jwtTokenProvider.validateRefreshToken(refreshToken)) {
                val newAccessToken = jwtTokenProvider.recreationAccessToken(refreshToken)
                if (newAccessToken != null) {
                    httpResponse.setHeader("Authorization", "Bearer $newAccessToken")
                    val authentication = jwtTokenProvider.getAuthentication(newAccessToken)
                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
        }

        chain?.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")

        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else {
            null
        }
    }

    private fun resolveRefreshToken(request: HttpServletRequest): String? {
        return request.getHeader("Refresh-Token")
    }

}