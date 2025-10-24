package com.youngmin.sns.service.dto

import com.youngmin.sns.domain.entity.Comment
import com.youngmin.sns.domain.entity.User

data class CommentResult(
    val comment: Comment,
    val user: User
)
