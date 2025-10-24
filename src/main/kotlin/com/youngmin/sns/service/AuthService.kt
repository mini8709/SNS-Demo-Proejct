package com.youngmin.sns.service

import com.youngmin.sns.config.auth.JwtTokenProvider
import com.youngmin.sns.config.exceptions.ConflictException
import com.youngmin.sns.config.exceptions.ErrorCode
import com.youngmin.sns.config.exceptions.NotFoundException
import com.youngmin.sns.config.exceptions.UnauthorizedException
import com.youngmin.sns.controller.dto.request.UserLoginRequestDto
import com.youngmin.sns.controller.dto.request.UserLogoutRequestDto
import com.youngmin.sns.controller.dto.request.UserRegisterRequestDto
import com.youngmin.sns.domain.entity.RefreshToken
import com.youngmin.sns.domain.entity.User
import com.youngmin.sns.repository.RefreshTokenRepository
import com.youngmin.sns.repository.UserRepository
import com.youngmin.sns.service.dto.AuthResult
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AuthService (
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: BCryptPasswordEncoder
) {
    @Transactional
    fun register(req: UserRegisterRequestDto): AuthResult {
        // 이메일 중복 체크
        if (userRepository.findByEmail(req.email) != null) {
            throw ConflictException(
                errorCode = ErrorCode.ROW_ALREADY_EXIST,
                message = "중복된 이메일 입니다.",
            )
        }

        // 비밀번호 암호화 & 유저 저장
        val user = userRepository.save(
            User(
                email = req.email,
                name = req.name,
                password = passwordEncoder.encode(req.password),
            )
        )

        // 토큰 생성
        val accessToken = jwtTokenProvider.generateAccessToken(user.id!!)
        val refreshToken = jwtTokenProvider.generateRefreshToken(user.id!!)

        // Refresh Token DB에 저장
        refreshTokenRepository.save(
            RefreshToken(
                userId = user.id!!,
                token = refreshToken,
                expiresAt = LocalDateTime.now().plusDays(7)
            )
        )

        return AuthResult(user, accessToken, refreshToken)
    }

    @Transactional
    fun login(req: UserLoginRequestDto): AuthResult {
        val user = userRepository.findByEmail(req.email)

        if (user == null) {
            throw NotFoundException(
                errorCode = ErrorCode.ROW_NOT_FOUND,
                message = "해당 유저가 존재하지 않습니다."
            )
        }

        if (user.password != passwordEncoder.encode(req.password)) {
            throw UnauthorizedException(
                errorCode = ErrorCode.INVALID_CREDENTIALS,
                message = "비밀번호가 일치하지 않습니다."
            )
        }

        val accessToken = jwtTokenProvider.generateAccessToken(user.id!!)
        val refreshToken = jwtTokenProvider.generateRefreshToken(user.id!!)

        // Refresh Token DB에 저장
        refreshTokenRepository.save(
            RefreshToken(
                userId = user.id!!,
                token = refreshToken,
                expiresAt = LocalDateTime.now().plusDays(7)
            )
        )

        return AuthResult(user, accessToken, refreshToken)
    }

    @Transactional
    fun logout(userId: Long, req: UserLogoutRequestDto) {
        val user = userRepository.findByIdOrNull(userId)

        if (user == null) {
            throw UnauthorizedException(
                errorCode = ErrorCode.INVALID_TOKEN,
                message = "해당 유저가 존재하지 않습니다."
            )
        }

        refreshTokenRepository.deleteByToken(req.refreshToken)
    }

    @Transactional
    fun refreshAccessToken(refreshToken: String): String {
        // JWT 자체 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw UnauthorizedException(
                ErrorCode.INVALID_TOKEN,
                "유효하지 않거나 만료된 토큰입니다."
            )
        }

        // DB에서 토큰 확인
        val savedToken = refreshTokenRepository.findByToken(refreshToken)
            ?: throw UnauthorizedException(
                ErrorCode.INVALID_TOKEN,
                "로그아웃되었거나 무효화된 토큰입니다."
            )

        // 만료 시간 확인
        if (savedToken.expiresAt.isBefore(LocalDateTime.now())) {
            refreshTokenRepository.deleteByToken(refreshToken)
            throw UnauthorizedException(
                ErrorCode.EXPIRED_TOKEN,
                "만료된 토큰입니다. 다시 로그인해주세요."
            )
        }

        // 새 Access Token 발급
        return jwtTokenProvider.generateAccessToken(savedToken.userId)
    }
}