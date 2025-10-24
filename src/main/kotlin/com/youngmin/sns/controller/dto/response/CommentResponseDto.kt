package com.youngmin.sns.controller.dto.response

import java.time.LocalDateTime

data class CommentResponseDto(
    val commentId: Long,
    val feedId: Long,
    val writer: Long,
    val name: String,
    val content: String,
    val date: LocalDateTime,
)
