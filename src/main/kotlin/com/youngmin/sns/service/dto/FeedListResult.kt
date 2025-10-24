package com.youngmin.sns.service.dto

data class FeedListResult(
    val feedList: List<FeedResult>,
    val nextCursor: Long?
)
