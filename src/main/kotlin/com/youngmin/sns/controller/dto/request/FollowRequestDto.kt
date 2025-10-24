package com.youngmin.sns.controller.dto.request

import jakarta.validation.constraints.NotBlank

data class FollowRequestDto(
    @field:NotBlank(message = "팔로잉 아이디는 필수입니다")
    val followingId: Long
)
