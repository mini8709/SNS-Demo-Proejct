package com.youngmin.sns.config.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.youngmin.sns.config.exceptions.ErrorCode
import com.youngmin.sns.config.exceptions.ErrorResponse
import com.youngmin.sns.config.exceptions.UnauthorizedException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            println("=== 요청 URL: ${request.requestURI} ===")
            // 1. Authorization 헤더에서 토큰 추출
            val token = resolveToken(request)

            // 2. 토큰 검증 및 인증 정보 설정
            if (token != null) {
                if (jwtTokenProvider.validateToken(token)) {
                    val userId = jwtTokenProvider.getUserIdFromToken(token)
                    val authentication = UsernamePasswordAuthenticationToken(userId, null, emptyList())
                    SecurityContextHolder.getContext().authentication = authentication
                } else {
                    println("validate 실패")
                    throw UnauthorizedException(
                        ErrorCode.INVALID_TOKEN,
                        "유효하지 않거나 만료된 토큰입니다."
                    )
                }
            } else {
                println("토큰이 없음")
            }
        } catch (e: UnauthorizedException) {
            handleUnauthorizedException(response, e)
            return
        } catch (e: Exception) {
            handleUnauthorizedException(
                response,
                UnauthorizedException(ErrorCode.INVALID_TOKEN, "인증 처리 중 오류가 발생했습니다: ${e.message}")
            )
            return
        }

        filterChain.doFilter(request, response)
    }

    private fun handleUnauthorizedException(response: HttpServletResponse, e: UnauthorizedException) {
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = "application/json;charset=UTF-8"

        val errorResponse = ErrorResponse(
            status = HttpStatus.UNAUTHORIZED.value(),
            errorCode = e.errorCode.code,
            message = e.message,
            path = ""
        )

        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        println("bearerToken: $bearerToken")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else null
    }
}