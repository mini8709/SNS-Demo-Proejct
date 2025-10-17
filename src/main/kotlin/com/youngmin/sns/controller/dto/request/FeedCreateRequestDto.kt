package com.youngmin.sns.controller.dto.request

import jakarta.validation.constraints.NotBlank

data class FeedCreateRequestDto(
    @field:NotBlank(message = "content는 필수입니다.")
    val content: String,
    val imageSrcList: List<String> = emptyList(),
)
