package com.youngmin.sns.controller.dto.response

import java.time.LocalDateTime

data class FeedResponseDto(
    val feedId: Long,
    val content: String,
    val writer: Long,
    val name: String,
    val feedImgList: List<String>,
    val date: LocalDateTime,
    val commentCount: Long,
    val likeCount: Long
)
