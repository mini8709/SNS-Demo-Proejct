package com.youngmin.sns.service.dto

import com.youngmin.sns.domain.entity.User

data class AuthResult(
    val user: User,
    val accessToken: String,
    val refreshToken: String
)
