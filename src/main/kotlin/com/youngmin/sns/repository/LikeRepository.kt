package com.youngmin.sns.repository

import com.youngmin.sns.domain.entity.Like
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface LikeRepository: JpaRepository<Like, Long> {
    fun deleteByFeedIdAndUserId(feedId: Long, userId: Long)

    fun deleteByFeedId(feedId: Long)

    fun countByFeedId(feedId: Long): Long

    @Query("SELECT l FROM Like l WHERE l.feedId = :feedId ORDER BY l.id DESC LIMIT :limit")
    fun findByFeedIdOrderByIdDesc(feedId: Long, limit: Int): List<Like>

    @Query("SELECT l FROM Like l WHERE l.feedId = :feedId AND l.id < :cursor ORDER BY l.id DESC LIMIT :limit")
    fun findByFeedIdAndIdLessThanOrderByIdDesc(feedId: Long, cursor: Long, limit: Int): List<Like>

    @Query("SELECT l.feedId, COUNT(l) FROM Like l WHERE l.feedId IN :feedIds GROUP BY l.feedId")
    fun countByFeedIdIn(feedIds: List<Long>): List<Array<Any>>
}