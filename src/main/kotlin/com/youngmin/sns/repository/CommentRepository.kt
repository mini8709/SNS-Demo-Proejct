package com.youngmin.sns.repository

import com.youngmin.sns.domain.entity.Comment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CommentRepository: JpaRepository<Comment, Long> {
    fun countByFeedId(feedId: Long): Long

    fun deleteByFeedId(feedId: Long)

    @Query("SELECT c FROM Comment c WHERE c.feedId = :feedId ORDER BY c.id DESC LIMIT :limit")
    fun findByFeedIdOrderByIdDesc(feedId: Long, limit: Int): List<Comment>

    @Query("SELECT c FROM Comment c WHERE c.feedId = :feedId AND c.id < :cursor ORDER BY c.id DESC LIMIT :limit")
    fun findByFeedIdAndIdLessThanOrderByIdDesc(feedId: Long, cursor: Long, limit: Int): List<Comment>

    @Query("SELECT c.feedId, COUNT(c) FROM Comment c WHERE c.feedId IN :feedIds GROUP BY c.feedId")
    fun countByFeedIdIn(feedIds: List<Long>): List<Array<Any>>
}