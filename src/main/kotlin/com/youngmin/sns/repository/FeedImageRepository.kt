package com.youngmin.sns.repository

import com.youngmin.sns.domain.entity.FeedImage
import org.springframework.data.jpa.repository.JpaRepository

interface FeedImageRepository: JpaRepository<FeedImage, Long> {
    fun deleteByFeedId(feedId: Long)
    fun findByFeedId(feedId: Long): List<FeedImage>
    fun findByFeedIdIn(feedIds: List<Long>): List<FeedImage>
}