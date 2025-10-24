package com.youngmin.sns.controller

import com.youngmin.sns.controller.dto.response.UserInfoResponseDto
import com.youngmin.sns.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {
    @GetMapping("/{userId}")
    fun getUserInfo(
        @PathVariable userId: Long
    ): ResponseEntity<UserInfoResponseDto> {
        val result = userService.getUserInfo(userId)
        return ResponseEntity.ok(
            UserInfoResponseDto(
                userId = result.user.id!!,
                email = result.user.email,
                name = result.user.name,
                followerCount = result.followerCount,
                followingCount = result.followingCount
            )
        )
    }
}