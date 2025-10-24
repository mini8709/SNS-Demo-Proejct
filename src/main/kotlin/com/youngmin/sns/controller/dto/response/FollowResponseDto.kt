package com.youngmin.sns.controller.dto.response

data class FollowResponseDto(
    val id: Long,
    val followerId: Long,
    val followingId: Long,
)