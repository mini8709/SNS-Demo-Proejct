package com.youngmin.sns.service

import com.youngmin.sns.config.exceptions.ErrorCode
import com.youngmin.sns.config.exceptions.NotFoundException
import com.youngmin.sns.config.exceptions.UnauthorizedException
import com.youngmin.sns.controller.dto.request.FollowRequestDto
import com.youngmin.sns.domain.entity.Follow
import com.youngmin.sns.repository.FollowRepository
import com.youngmin.sns.repository.UserRepository
import com.youngmin.sns.service.dto.FollowListResult
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FollowService (
    private val followRepository: FollowRepository,
    private val userRepository: UserRepository,
){
    @Transactional
    fun followUser(userId: Long, req: FollowRequestDto): Follow {
        val follower = userRepository.findByIdOrNull(userId)

        if (follower == null) {
            throw UnauthorizedException(
                ErrorCode.USER_NOT_FOUND, "유저가 존재하지 않습니다."
            )
        }

        val following = userRepository.findByIdOrNull(req.followingId)

        if (following == null) {
            throw NotFoundException(
                ErrorCode.ROW_NOT_FOUND, "유저가 존재하지 않습니다."
            )
        }

        return followRepository.save(
            Follow(
                followerId = follower.id!!,
                followingId = following.id!!
            )
        )
    }

    @Transactional
    fun unfollowUser(userId: Long, req: FollowRequestDto) {
        val follower = userRepository.findByIdOrNull(userId)

        if (follower == null) {
            throw UnauthorizedException(
                ErrorCode.USER_NOT_FOUND, "유저가 존재하지 않습니다."
            )
        }

        val following = userRepository.findByIdOrNull(req.followingId)

        if (following == null) {
            throw NotFoundException(
                ErrorCode.ROW_NOT_FOUND, "유저가 존재하지 않습니다."
            )
        }

        val follow = followRepository.findByFollowerIdAndFollowingId(
            follower.id!!, following.id!!
        )

        if (follow == null) {
            throw NotFoundException(
                ErrorCode.ROW_NOT_FOUND, "팔로우한 유저가 아닙니다."
            )
        }

        followRepository.delete(follow)
    }

    @Transactional
    fun getFollowerList(userId: Long, cursor: Long?, size: Int): FollowListResult {
        val user = userRepository.findByIdOrNull(userId)

        if (user == null) {
            throw UnauthorizedException(
                ErrorCode.USER_NOT_FOUND, "유저가 존재하지 않습니다."
            )
        }

        val follows = if (cursor == null) {
            followRepository.findByFollowingIdOrderByIdDesc(user.id!!, size + 1)
        } else {
            followRepository.findByFollowingIdAndIdLessThanOrderByIdDesc(user.id!!, cursor, size + 1)
        }

        val hasNext = follows.size > size
        val items = if (hasNext) follows.dropLast(1) else follows
        val nextCursor = if (hasNext) items.last().id else null

        // follower 유저들의 ID 추출
        val followerIds = items.map { it.followerId }
        val users = userRepository.findByIdIn(followerIds)
        val userMap = users.associateBy { it.id!! }

        // Follow 순서대로 User 정렬
        val orderedUsers = items.map { follow ->
            userMap[follow.followerId]!!
        }

        return FollowListResult(
            users = orderedUsers,
            nextCursor = nextCursor
        )
    }

    @Transactional
    fun getFollowingList(userId: Long, cursor: Long?, size: Int): FollowListResult {
        val user = userRepository.findByIdOrNull(userId)

        if (user == null) {
            throw UnauthorizedException(
                ErrorCode.USER_NOT_FOUND, "유저가 존재하지 않습니다."
            )
        }

        val follows = if (cursor == null) {
            followRepository.findByFollowerIdOrderByIdDesc(user.id!!, size + 1)
        } else {
            followRepository.findByFollowerIdAndIdLessThanOrderByIdDesc(user.id!!, cursor, size + 1)
        }

        val hasNext = follows.size > size
        val items = if (hasNext) follows.dropLast(1) else follows
        val nextCursor = if (hasNext) items.last().id else null

        // following 유저들의 ID 추출
        val followingIds = items.map { it.followingId }
        val users = userRepository.findByIdIn(followingIds)
        val userMap = users.associateBy { it.id!! }

        // Follow 순서대로 User 정렬
        val orderedUsers = items.map { follow ->
            userMap[follow.followingId]!!
        }

        return FollowListResult(
            users = orderedUsers,
            nextCursor = nextCursor
        )
    }
}
