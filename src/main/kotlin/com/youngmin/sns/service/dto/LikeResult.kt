package com.youngmin.sns.service.dto

import com.youngmin.sns.domain.entity.Like
import com.youngmin.sns.domain.entity.User

data class LikeResult(
    val like: Like,
    val user: User
)
