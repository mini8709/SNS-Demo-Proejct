package com.youngmin.sns.controller.dto.request

import jakarta.validation.constraints.NotBlank

data class UserLogoutRequestDto(
    @field:NotBlank(message = "refreshToken은 필수입니다.")
    val refreshToken: String
)
