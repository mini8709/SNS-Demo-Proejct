package com.youngmin.sns.controller.dto.response

data class UserInfoResponseDto (
    val userId: Long,
    val email: String,
    val name: String,
    val followerCount: Long,
    val followingCount: Long
)