package com.youngmin.sns.service.dto

data class CommentListResult(
    val commentList: List<CommentResult>,
    val nextCursor: Long?
)
