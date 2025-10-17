package com.youngmin.sns.controller.dto.response

data class LikeResponseDto(
    val likeId: Long,
    val feedId: Long,
    val userId: Long,
    val name: String,
)
