package com.youngmin.sns.repository

import com.youngmin.sns.domain.entity.FollowCount
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface FollowCountRepository : JpaRepository<FollowCount, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT fc FROM FollowCount fc WHERE fc.id = :id")
    fun findByIdForUpdate(id: Long): FollowCount?

    @Modifying
    @Query("UPDATE FollowCount fc SET fc.followerCount = fc.followerCount + :delta WHERE fc.id = :id")
    fun updateFollowerCount(id: Long, delta: Int)

    @Modifying
    @Query("UPDATE FollowCount fc SET fc.followingCount = fc.followingCount + :delta WHERE fc.id = :id")
    fun updateFollowingCount(id: Long, delta: Int)
}