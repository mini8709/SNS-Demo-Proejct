package com.youngmin.sns.repository

import com.youngmin.sns.domain.entity.Feed
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface FeedRepository: JpaRepository<Feed, Long> {
    @Query("SELECT f FROM Feed f WHERE f.writer = :writer ORDER BY f.id DESC LIMIT :limit")
    fun findByWriterOrderByIdDesc(writer: Long, limit: Int): List<Feed>

    @Query("SELECT f FROM Feed f WHERE f.writer = :writer AND f.id < :cursor ORDER BY f.id DESC LIMIT :limit")
    fun findByWriterAndIdLessThanOrderByIdDesc(writer: Long, cursor: Long, limit: Int): List<Feed>

    @Query("SELECT f FROM Feed f WHERE f.writer IN (SELECT fl.followingId FROM Follow fl WHERE fl.followerId = :userId) ORDER BY f.id DESC LIMIT :limit")
    fun findTimelineFeedsOrderByIdDesc(userId: Long, limit: Int): List<Feed>

    @Query("SELECT f FROM Feed f WHERE f.writer IN (SELECT fl.followingId FROM Follow fl WHERE fl.followerId = :userId) AND f.id < :cursor ORDER BY f.id DESC LIMIT :limit")
    fun findTimelineFeedsAndIdLessThanOrderByIdDesc(userId: Long, cursor: Long, limit: Int): List<Feed>
}