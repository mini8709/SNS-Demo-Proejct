package com.youngmin.sns.repository

import com.youngmin.sns.domain.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun findByIdIn(ids: List<Long>): List<User>
}
