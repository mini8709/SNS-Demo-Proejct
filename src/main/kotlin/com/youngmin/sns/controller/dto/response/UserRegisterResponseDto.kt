package com.youngmin.sns.controller.dto.response

data class UserRegisterResponseDto(
    val id: Long,
    val email: String,
    val name: String,
    val accessToken: String,
    val refreshToken: String
)
