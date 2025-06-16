package hs.kr.gbsw.doumi

import hs.kr.gbsw.doumi.auth.jwt.JwtAuthenticationFilter
import hs.kr.gbsw.doumi.auth.jwt.JwtTokenProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/login/oauth2/**",
                    "/api/users",
                    "/api/users/login",
                    "/api/users/verify",
                    "/api/users/refresh",
                    "/api/email/send",
                    "/api/board/posts",
                    "/api/board/posts/**",
                    "/api/map"
                ).permitAll()
                    .requestMatchers("/api/users/info").authenticated()
                    .anyRequest().permitAll()
            }
            .addFilterBefore(
                JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter::class.java
            )

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()

        configuration.allowedOrigins = listOf("http://localhost:8081")
        configuration.allowedMethods = listOf("POST", "GET", "PUT", "DELETE")
        configuration.allowedHeaders = listOf("Authorization", "Content-Type", "X-Requested-With")
        configuration.exposedHeaders = listOf("Authorization", "Content-Type")

        configuration.allowCredentials = true

        val source= UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder =
        PasswordEncoderFactories.createDelegatingPasswordEncoder()

}