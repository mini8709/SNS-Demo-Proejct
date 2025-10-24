package com.youngmin.sns.service.dto

data class LikeListResult(
    val likeList: List<LikeResult>,
    val nextCursor: Long?,
)
