package com.youngmin.sns.service.dto

import com.youngmin.sns.domain.entity.User

data class FollowListResult(
    val users: List<User>,
    val nextCursor: Long?
)
