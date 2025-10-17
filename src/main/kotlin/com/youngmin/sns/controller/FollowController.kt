package com.youngmin.sns.controller

import com.youngmin.sns.controller.dto.request.FollowRequestDto
import com.youngmin.sns.controller.dto.response.FollowResponseDto
import com.youngmin.sns.controller.dto.response.FollowUserResponseDto
import com.youngmin.sns.common.pagination.CursorPaginated
import com.youngmin.sns.service.FollowService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/follow")
class FollowController(
    private val followService: FollowService
) {

    @PostMapping
    fun followUser(
        @AuthenticationPrincipal userId: Long,
        @RequestBody req: FollowRequestDto
    ): ResponseEntity<FollowResponseDto> {
        val follow = followService.followUser(userId, req)
        return ResponseEntity.ok(
            FollowResponseDto(
                id = follow.id!!,
                followerId = follow.followerId,
                followingId = follow.followingId
            )
        )
    }

    @DeleteMapping
    fun unfollowUser(
        @AuthenticationPrincipal userId: Long,
        @RequestBody req: FollowRequestDto
    ): ResponseEntity<Void> {
        followService.unfollowUser(userId, req)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/followers")
    fun getFollowerList(
        @AuthenticationPrincipal userId: Long,
        @RequestParam(required = false) cursor: Long?,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<CursorPaginated<FollowUserResponseDto>> {
        val result = followService.getFollowerList(userId, cursor, size)
        return ResponseEntity.ok(
            CursorPaginated(
                items = result.users.map { user ->
                    FollowUserResponseDto(
                        userId = user.id!!,
                        name = user.name
                    )
                },
                nextCursor = result.nextCursor
            )
        )
    }

    @GetMapping("/following")
    fun getFollowingList(
        @AuthenticationPrincipal userId: Long,
        @RequestParam(required = false) cursor: Long?,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<CursorPaginated<FollowUserResponseDto>> {
        val result = followService.getFollowingList(userId, cursor, size)
        return ResponseEntity.ok(
            CursorPaginated(
                items = result.users.map { user ->
                    FollowUserResponseDto(
                        userId = user.id!!,
                        name = user.name
                    )
                },
                nextCursor = result.nextCursor
            )
        )
    }
}