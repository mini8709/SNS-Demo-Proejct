package com.youngmin.sns.service.dto

import com.youngmin.sns.domain.entity.User

data class UserInfoResult(
    val user: User,
    val followerCount: Long,
    val followingCount: Long
)
