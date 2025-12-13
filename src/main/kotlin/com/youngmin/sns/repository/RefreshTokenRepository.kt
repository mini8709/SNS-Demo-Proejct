package com.youngmin.sns.repository

import com.youngmin.sns.domain.entity.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    fun findByToken(token: String): RefreshToken?
    fun deleteByToken(token: String)
    fun deleteByUserId(userId: Long)
}
