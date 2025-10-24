package com.youngmin.sns.controller

import com.youngmin.sns.common.pagination.CursorPaginated
import com.youngmin.sns.controller.dto.request.CommentRequestDto
import com.youngmin.sns.controller.dto.request.FeedCreateRequestDto
import com.youngmin.sns.controller.dto.request.FeedModifyRequestDto
import com.youngmin.sns.controller.dto.response.CommentResponseDto
import com.youngmin.sns.controller.dto.response.FeedResponseDto
import com.youngmin.sns.controller.dto.response.LikeResponseDto
import com.youngmin.sns.service.FeedService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/feeds")
class FeedController(
    private val feedService: FeedService
) {
    @GetMapping("/user/{targetUserId}")
    fun getUserFeedList(
        @AuthenticationPrincipal userId: Long,
        @PathVariable targetUserId: Long,
        @RequestParam(required = false) cursor: Long?,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<CursorPaginated<FeedResponseDto>> {
        val feedListResult = feedService.getUserFeedList(userId, targetUserId, cursor, size)

        return ResponseEntity.ok(
            CursorPaginated(
                items = feedListResult.feedList.map {
                    FeedResponseDto(
                        feedId = it.feed.id!!,
                        content = it.feed.content,
                        writer = it.writer.id!!,
                        name = it.writer.name,
                        feedImgList = it.feedImgList.map { img -> img.imgSrc },
                        date = it.feed.date!!,
                        commentCount = it.commentCount,
                        likeCount = it.likeCount
                    )
                },
                nextCursor = feedListResult.nextCursor
            )
        )
    }

    @GetMapping("/timeline")
    fun getTimelineFeedList(
        @AuthenticationPrincipal userId: Long,
        @RequestParam(required = false) cursor: Long?,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<CursorPaginated<FeedResponseDto>> {
        val feedListResult = feedService.getTimelineFeedList(userId, cursor, size)

        return ResponseEntity.ok(
            CursorPaginated(
                items = feedListResult.feedList.map {
                    FeedResponseDto(
                        feedId = it.feed.id!!,
                        content = it.feed.content,
                        writer = it.writer.id!!,
                        name = it.writer.name,
                        feedImgList = it.feedImgList.map { img -> img.imgSrc },
                        date = it.feed.date!!,
                        commentCount = it.commentCount,
                        likeCount = it.likeCount
                    )
                },
                nextCursor = feedListResult.nextCursor
            )
        )
    }

    @PostMapping
    fun createFeed(
        @AuthenticationPrincipal userId: Long,
        @RequestBody req: FeedCreateRequestDto
    ): ResponseEntity<FeedResponseDto> {
        val feedResult = feedService.createFeed(userId, req)
        return ResponseEntity.ok(
            FeedResponseDto(
                feedId = feedResult.feed.id!!,
                content = feedResult.feed.content,
                writer = feedResult.writer.id!!,
                name = feedResult.writer.name,
                feedImgList = feedResult.feedImgList.map { it.imgSrc },
                date = feedResult.feed.date!!,
                commentCount = feedResult.commentCount,
                likeCount = feedResult.likeCount
            )
        )
    }

    @PutMapping("/{feedId}")
    fun modifyFeed(
        @AuthenticationPrincipal userId: Long,
        @PathVariable feedId: Long,
        @RequestBody req: FeedModifyRequestDto
    ): ResponseEntity<FeedResponseDto> {
        val feedResult = feedService.modifyFeed(userId, feedId, req)
        return ResponseEntity.ok(
            FeedResponseDto(
                feedId = feedResult.feed.id!!,
                content = feedResult.feed.content,
                writer = feedResult.writer.id!!,
                name = feedResult.writer.name,
                feedImgList = feedResult.feedImgList.map { it.imgSrc },
                date = feedResult.feed.date!!,
                commentCount = feedResult.commentCount,
                likeCount = feedResult.likeCount
            )
        )
    }

    @DeleteMapping("/{feedId}")
    fun deleteFeed(
        @AuthenticationPrincipal userId: Long,
        @PathVariable feedId: Long
    ): ResponseEntity<Void> {
        feedService.deleteFeed(userId, feedId)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{feedId}/comments")
    fun getFeedCommentList(
        @AuthenticationPrincipal userId: Long,
        @PathVariable feedId: Long,
        @RequestParam(required = false) cursor: Long?,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<CursorPaginated<CommentResponseDto>> {
        val feedListResult = feedService.getFeedCommentList(userId, feedId, cursor, size)

        return ResponseEntity.ok(
            CursorPaginated(
                items = feedListResult.commentList.map {
                    CommentResponseDto(
                        commentId = it.comment.id!!,
                        feedId = it.comment.feedId,
                        writer = it.comment.writer,
                        name = it.user.name,
                        content = it.comment.content,
                        date = it.comment.date!!
                    )
                },
                nextCursor = feedListResult.nextCursor
            )
        )
    }

    @PostMapping("/{feedId}/comments")
    fun createFeedComment(
        @AuthenticationPrincipal userId: Long,
        @PathVariable feedId: Long,
        @RequestBody req: CommentRequestDto
    ): ResponseEntity<CommentResponseDto> {
        val commentResult = feedService.createFeedComment(userId, feedId, req)

        return ResponseEntity.ok(
            CommentResponseDto(
                commentId = commentResult.comment.id!!,
                feedId = commentResult.comment.feedId,
                writer = commentResult.comment.writer,
                name = commentResult.user.name,
                content = commentResult.comment.content,
                date = commentResult.comment.date!!
            )
        )
    }

    @PutMapping("/{feedId}/comments/{commentId}")
    fun modifyFeedComment(
        @AuthenticationPrincipal userId: Long,
        @PathVariable feedId: Long,
        @PathVariable commentId: Long,
        @RequestBody req: CommentRequestDto
    ): ResponseEntity<CommentResponseDto> {
        val commentResult = feedService.modifyFeedComment(userId, feedId, commentId, req)
        return ResponseEntity.ok(
            CommentResponseDto(
                commentId = commentResult.comment.id!!,
                feedId = commentResult.comment.feedId,
                writer = commentResult.comment.writer,
                name = commentResult.user.name,
                content = commentResult.comment.content,
                date = commentResult.comment.date!!
            )
        )
    }

    @DeleteMapping("/{feedId}/comments/{commentId}")
    fun deleteFeedComment(
        @AuthenticationPrincipal userId: Long,
        @PathVariable feedId: Long,
        @PathVariable commentId: Long
    ): ResponseEntity<Void> {
        feedService.deleteFeedComment(userId, feedId, commentId)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/{feedId}/likes")
    fun createFeedLike(
        @AuthenticationPrincipal userId: Long,
        @PathVariable feedId: Long
    ): ResponseEntity<Void> {
        feedService.createFeedLike(userId, feedId)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{feedId}/likes")
    fun deleteFeedLike(
        @AuthenticationPrincipal userId: Long,
        @PathVariable feedId: Long
    ): ResponseEntity<Void> {
        feedService.deleteFeedLike(userId, feedId)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{feedId}/likes")
    fun getFeedLikeList(
        @AuthenticationPrincipal userId: Long,
        @PathVariable feedId: Long,
        @RequestParam(required = false) cursor: Long?,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<CursorPaginated<LikeResponseDto>> {
        val likeListResult = feedService.getFeedLikeList(userId, feedId, cursor, size)

        return ResponseEntity.ok(
            CursorPaginated(
                items = likeListResult.likeList.map {
                    LikeResponseDto(
                        likeId = it.like.id!!,
                        feedId = it.like.feedId,
                        userId = it.like.userId,
                        name = it.user.name
                    )
                },
                nextCursor = likeListResult.nextCursor
            )
        )
    }
}
