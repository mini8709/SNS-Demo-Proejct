package com.youngmin.sns.service

import com.youngmin.sns.config.exceptions.ErrorCode
import com.youngmin.sns.config.exceptions.NotFoundException
import com.youngmin.sns.repository.FollowRepository
import com.youngmin.sns.repository.UserRepository
import com.youngmin.sns.service.dto.UserInfoResult
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService (
    private val userRepository: UserRepository,
    private val followRepository: FollowRepository
){
    @Transactional
    fun getUserInfo(userId: Long): UserInfoResult {
        val findUser = userRepository.findByIdOrNull(userId)

        if (findUser == null) {
            throw NotFoundException(
                errorCode = ErrorCode.ROW_NOT_FOUND,
                message = "해당 유저가 존재하지 않습니다."
            )
        }

        val followingCount = followRepository.countByFollowerId(followerId = findUser.id!!)
        val followerCount = followRepository.countByFollowingId(followingId = findUser.id!!)

        return UserInfoResult(
            findUser, followerCount, followingCount
        )
    }
}
