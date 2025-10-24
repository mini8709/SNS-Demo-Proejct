package com.youngmin.sns.service.dto

import com.youngmin.sns.domain.entity.Feed
import com.youngmin.sns.domain.entity.FeedImage
import com.youngmin.sns.domain.entity.User

data class FeedResult(
    val feed: Feed,
    val feedImgList: List<FeedImage>,
    val writer: User,
    val commentCount: Long,
    val likeCount: Long
)
