package com.youngmin.sns.repository

import com.youngmin.sns.domain.entity.Follow
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface FollowRepository: JpaRepository<Follow, Long> {
    fun findByFollowerIdAndFollowingId(followerId: Long, followingId: Long): Follow?

    fun countByFollowerId(followerId: Long): Long

    fun countByFollowingId(followingId: Long): Long

    @Query("SELECT f FROM Follow f WHERE f.followingId = :followingId ORDER BY f.id DESC LIMIT :limit")
    fun findByFollowingIdOrderByIdDesc(followingId: Long, limit: Int): List<Follow>

    @Query("SELECT f FROM Follow f WHERE f.followingId = :followingId AND f.id < :cursor ORDER BY f.id DESC LIMIT :limit")
    fun findByFollowingIdAndIdLessThanOrderByIdDesc(followingId: Long, cursor: Long, limit: Int): List<Follow>

    @Query("SELECT f FROM Follow f WHERE f.followerId = :followerId ORDER BY f.id DESC LIMIT :limit")
    fun findByFollowerIdOrderByIdDesc(followerId: Long, limit: Int): List<Follow>

    @Query("SELECT f FROM Follow f WHERE f.followerId = :followerId AND f.id < :cursor ORDER BY f.id DESC LIMIT :limit")
    fun findByFollowerIdAndIdLessThanOrderByIdDesc(followerId: Long, cursor: Long, limit: Int): List<Follow>
}