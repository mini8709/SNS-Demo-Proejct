package com.youngmin.sns.controller

import com.youngmin.sns.controller.dto.request.RefreshTokenRequestDto
import com.youngmin.sns.controller.dto.request.UserLoginRequestDto
import com.youngmin.sns.controller.dto.request.UserLogoutRequestDto
import com.youngmin.sns.controller.dto.request.UserRegisterRequestDto
import com.youngmin.sns.controller.dto.response.AccessTokenResponseDto
import com.youngmin.sns.controller.dto.response.UserRegisterResponseDto
import com.youngmin.sns.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/register")
    fun register(@RequestBody req: UserRegisterRequestDto): ResponseEntity<UserRegisterResponseDto> {
        val result = authService.register(req)
        return ResponseEntity.ok(
            UserRegisterResponseDto(
                id = result.user.id!!,
                email = result.user.email,
                name = result.user.name,
                accessToken = result.accessToken,
                refreshToken = result.refreshToken
            )
        )
    }

    @PostMapping("/login")
    fun login(@RequestBody req: UserLoginRequestDto): ResponseEntity<UserRegisterResponseDto> {
        val result = authService.login(req)
        return ResponseEntity.ok(
            UserRegisterResponseDto(
                id = result.user.id!!,
                email = result.user.email,
                name = result.user.name,
                accessToken = result.accessToken,
                refreshToken = result.refreshToken
            )
        )
    }

    @PostMapping("/logout")
    fun logout(
        @AuthenticationPrincipal userId: Long,
        @RequestBody req: UserLogoutRequestDto
    ): ResponseEntity<Void> {
        authService.logout(userId, req)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/refresh")
    fun refreshAccessToken(
        @RequestBody req: RefreshTokenRequestDto
    ): ResponseEntity<AccessTokenResponseDto> {
        val accessToken = authService.refreshAccessToken(req.refreshToken)
        return ResponseEntity.ok(
            AccessTokenResponseDto(accessToken)
        )
    }
}