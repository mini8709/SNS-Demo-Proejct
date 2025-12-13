package com.youngmin.sns.service

import com.youngmin.sns.config.exceptions.ErrorCode
import com.youngmin.sns.config.exceptions.NotFoundException
import com.youngmin.sns.config.exceptions.UnauthorizedException
import com.youngmin.sns.controller.dto.request.CommentRequestDto
import com.youngmin.sns.controller.dto.request.FeedCreateRequestDto
import com.youngmin.sns.controller.dto.request.FeedModifyRequestDto
import com.youngmin.sns.domain.entity.Comment
import com.youngmin.sns.domain.entity.Feed
import com.youngmin.sns.domain.entity.FeedImage
import com.youngmin.sns.domain.entity.Like
import com.youngmin.sns.repository.CommentRepository
import com.youngmin.sns.repository.FeedImageRepository
import com.youngmin.sns.repository.FeedRepository
import com.youngmin.sns.repository.LikeRepository
import com.youngmin.sns.repository.UserRepository
import com.youngmin.sns.service.dto.CommentListResult
import com.youngmin.sns.service.dto.CommentResult
import com.youngmin.sns.service.dto.FeedListResult
import com.youngmin.sns.service.dto.FeedResult
import com.youngmin.sns.service.dto.LikeListResult
import com.youngmin.sns.service.dto.LikeResult
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FeedService (
    private val userRepository: UserRepository,
    private val feedRepository: FeedRepository,
    private val feedImageRepository: FeedImageRepository,
    private val commentRepository: CommentRepository,
    private val likeRepository: LikeRepository,
){
    @Transactional(readOnly = true)
    fun getUserFeedList(userId: Long, targetUserId: Long, cursor: Long?, size: Int): FeedListResult {
        val user = userRepository.findByIdOrNull(userId)

        if (user == null) {
            throw UnauthorizedException(
                ErrorCode.USER_NOT_FOUND, "유저가 존재하지 않습니다."
            )
        }

        val targetUser = userRepository.findByIdOrNull(targetUserId)

        if (targetUser == null) {
            throw NotFoundException(
                ErrorCode.ROW_NOT_FOUND,
                "해당 유저가 존재하지 않습니다."
            )
        }

        val feeds = if (cursor == null) {
            feedRepository.findByWriterOrderByIdDesc(targetUser.id!!, size + 1)
        } else {
            feedRepository.findByWriterAndIdLessThanOrderByIdDesc(targetUser.id!!, cursor, size + 1)
        }

        val hasNext = feeds.size > size
        val items = if (hasNext) feeds.dropLast(1) else feeds
        val nextCursor = if (hasNext) items.last().id else null

        val feedIds = items.map { it.id!! }

        // 피드 이미지들을 batch로 가져오기
        val feedImages = feedImageRepository.findByFeedIdIn(feedIds)
        val feedImageGroups = feedImages.groupBy { it.feedId }

        // 댓글 수를 batch로 가져오기
        val commentCounts = commentRepository.countByFeedIdIn(feedIds)
        val commentCountMap = commentCounts.associate { it[0] as Long to it[1] as Long }

        // 좋아요 수를 batch로 가져오기
        val likeCounts = likeRepository.countByFeedIdIn(feedIds)
        val likeCountMap = likeCounts.associate { it[0] as Long to it[1] as Long }

        // FeedResult 리스트 생성
        val feedResults = items.map { feed ->
            FeedResult(
                feed = feed,
                feedImgList = feedImageGroups[feed.id] ?: emptyList(),
                writer = targetUser,
                commentCount = commentCountMap[feed.id] ?: 0,
                likeCount = likeCountMap[feed.id] ?: 0
            )
        }

        return FeedListResult(
            feedList = feedResults,
            nextCursor = nextCursor
        )
    }

    @Transactional(readOnly = true)
    fun getTimelineFeedList(userId: Long, cursor: Long?, size: Int): FeedListResult {
        val user = userRepository.findByIdOrNull(userId)

        if (user == null) {
            throw UnauthorizedException(
                ErrorCode.USER_NOT_FOUND, "유저가 존재하지 않습니다."
            )
        }

        // 서브쿼리를 사용해 팔로잉 유저들의 피드를 한 번에 조회
        val feeds = if (cursor == null) {
            feedRepository.findTimelineFeedsOrderByIdDesc(userId, size + 1)
        } else {
            feedRepository.findTimelineFeedsAndIdLessThanOrderByIdDesc(userId, cursor, size + 1)
        }

        val hasNext = feeds.size > size
        val items = if (hasNext) feeds.dropLast(1) else feeds
        val nextCursor = if (hasNext) items.last().id else null

        // 피드가 없으면 빈 리스트 반환
        if (items.isEmpty()) {
            return FeedListResult(
                feedList = emptyList(),
                nextCursor = null
            )
        }

        val feedIds = items.map { it.id!! }

        // 피드 이미지들을 batch로 가져오기
        val feedImages = feedImageRepository.findByFeedIdIn(feedIds)
        val feedImageGroups = feedImages.groupBy { it.feedId }

        // 댓글 수를 batch로 가져오기
        val commentCounts = commentRepository.countByFeedIdIn(feedIds)
        val commentCountMap = commentCounts.associate { it[0] as Long to it[1] as Long }

        // 좋아요 수를 batch로 가져오기
        val likeCounts = likeRepository.countByFeedIdIn(feedIds)
        val likeCountMap = likeCounts.associate { it[0] as Long to it[1] as Long }

        // 피드 작성자들의 User 정보 조회
        val writerIds = items.map { it.writer }.distinct()
        val writers = userRepository.findByIdIn(writerIds)
        val writerMap = writers.associateBy { it.id!! }

        // FeedResult 리스트 생성
        val feedResults = items.map { feed ->
            FeedResult(
                feed = feed,
                feedImgList = feedImageGroups[feed.id] ?: emptyList(),
                writer = writerMap[feed.writer]!!,
                commentCount = commentCountMap[feed.id] ?: 0,
                likeCount = likeCountMap[feed.id] ?: 0
            )
        }

        return FeedListResult(
            feedList = feedResults,
            nextCursor = nextCursor
        )
    }

    @Transactional
    fun createFeed(userId: Long, req: FeedCreateRequestDto): FeedResult {
        val user = userRepository.findByIdOrNull(userId)

        if (user == null) {
            throw UnauthorizedException(
                errorCode = ErrorCode.USER_NOT_FOUND,
                message = "유저가 존재하지 않습니다.",
            )
        }

        val feed = feedRepository.save(
            Feed(
                writer = user.id!!,
                content = req.content,
            )
        )

        val feedImgList = mutableListOf<FeedImage>()
        req.imageSrcList.forEach { src ->
            val feedImage = feedImageRepository.save(
                FeedImage(
                    feedId = feed.id!!,
                    imgSrc = src
                )
            )
            feedImgList.add(feedImage)
        }

        return FeedResult(
            feed = feed,
            feedImgList = feedImgList,
            writer = user,
            commentCount = 0,
            likeCount = 0
        )
    }

    @Transactional
    fun modifyFeed(userId: Long, feedId: Long, req: FeedModifyRequestDto): FeedResult {
        val user = userRepository.findByIdOrNull(userId)

        if (user == null) {
            throw UnauthorizedException(
                errorCode = ErrorCode.USER_NOT_FOUND,
                message = "유저가 존재하지 않습니다.",
            )
        }

        val feed = feedRepository.findByIdOrNull(feedId)

        if (feed == null) {
            throw NotFoundException(
                errorCode = ErrorCode.ROW_NOT_FOUND,
                message = "피드가 존재하지 않습니다.",
            )
        }

        if (feed.writer != user.id) {
            throw UnauthorizedException(
                errorCode = ErrorCode.NOT_AUTHORIZED,
                message = "피드 작성자가 아닙니다.",
            )
        }

        // content 수정
        feed.content = req.content

        val feedImgList = if (req.isImageChanged) {
            // 기존 이미지 삭제
            feedImageRepository.deleteByFeedId(feedId)

            // 새로운 이미지 생성
            val newFeedImgList = mutableListOf<FeedImage>()
            req.imageSrcList.forEach { src ->
                val feedImage = feedImageRepository.save(
                    FeedImage(
                        feedId = feed.id!!,
                        imgSrc = src
                    )
                )
                newFeedImgList.add(feedImage)
            }
            newFeedImgList
        } else {
            // 기존 이미지 유지
            feedImageRepository.findByFeedId(feedId)
        }

        val commentCount = commentRepository.countByFeedId(feedId)
        val likeCount = likeRepository.countByFeedId(feedId)

        return FeedResult(
            feed = feed,
            feedImgList = feedImgList,
            writer = user,
            commentCount = commentCount,
            likeCount = likeCount,
        )
    }

    @Transactional
    fun deleteFeed(userId: Long, feedId: Long) {
        val user = userRepository.findByIdOrNull(userId)

        if (user == null) {
            throw UnauthorizedException(
                errorCode = ErrorCode.USER_NOT_FOUND,
                message = "유저가 존재하지 않습니다.",
            )
        }

        val feed = feedRepository.findByIdOrNull(feedId)

        if (feed == null) {
            throw NotFoundException(
                errorCode = ErrorCode.ROW_NOT_FOUND,
                message = "피드가 존재하지 않습니다.",
            )
        }

        if (feed.writer != user.id) {
            throw UnauthorizedException(
                errorCode = ErrorCode.NOT_AUTHORIZED,
                message = "피드 작성자가 아닙니다.",
            )
        }

        // 연관된 데이터 삭제
        feedImageRepository.deleteByFeedId(feedId)
        commentRepository.deleteByFeedId(feedId)
        likeRepository.deleteByFeedId(feedId)

        // Feed 삭제
        feedRepository.delete(feed)
    }

    @Transactional(readOnly = true)
    fun getFeedCommentList(userId: Long, feedId: Long, cursor: Long?, size: Int): CommentListResult {
        val user = userRepository.findByIdOrNull(userId)

        if (user == null) {
            throw UnauthorizedException(
                errorCode = ErrorCode.USER_NOT_FOUND,
                message = "유저가 존재하지 않습니다.",
            )
        }

        val feed = feedRepository.findByIdOrNull(feedId)

        if (feed == null) {
            throw NotFoundException(
                errorCode = ErrorCode.ROW_NOT_FOUND,
                message = "피드가 존재하지 않습니다.",
            )
        }

        val comments = if (cursor == null) {
            commentRepository.findByFeedIdOrderByIdDesc(feedId, size + 1)
        } else {
            commentRepository.findByFeedIdAndIdLessThanOrderByIdDesc(feedId, cursor, size + 1)
        }

        val hasNext = comments.size > size
        val items = if (hasNext) comments.dropLast(1) else comments
        val nextCursor = if (hasNext) items.last().id else null

        // 댓글 작성자들의 User 정보 조회
        val writerIds = items.map { it.writer }
        val users = userRepository.findByIdIn(writerIds)
        val userMap = users.associateBy { it.id!! }

        val commentResults = items.map { comment ->
            CommentResult(
                comment = comment,
                user = userMap[comment.writer]!!
            )
        }

        return CommentListResult(
            commentList = commentResults,
            nextCursor = nextCursor
        )
    }

    @Transactional
    fun createFeedComment(userId: Long, feedId: Long, req: CommentRequestDto): CommentResult {
        val user = userRepository.findByIdOrNull(userId)

        if (user == null) {
            throw UnauthorizedException(
                errorCode = ErrorCode.USER_NOT_FOUND,
                message = "유저가 존재하지 않습니다.",
            )
        }

        val feed = feedRepository.findByIdOrNull(feedId)

        if (feed == null) {
            throw NotFoundException(
                errorCode = ErrorCode.ROW_NOT_FOUND,
                message = "피드가 존재하지 않습니다.",
            )
        }

        val comment = commentRepository.save(
            Comment(
                feedId = feed.id!!,
                writer = user.id!!,
                content = req.comment
            )
        )

        return CommentResult(
            comment = comment,
            user = user
        )
    }

    @Transactional
    fun modifyFeedComment(
        userId: Long, feedId: Long, commentId: Long, req: CommentRequestDto
    ): CommentResult {
        val user = userRepository.findByIdOrNull(userId)

        if (user == null) {
            throw UnauthorizedException(
                errorCode = ErrorCode.USER_NOT_FOUND,
                message = "유저가 존재하지 않습니다.",
            )
        }

        val feed = feedRepository.findByIdOrNull(feedId)

        if (feed == null) {
            throw NotFoundException(
                errorCode = ErrorCode.ROW_NOT_FOUND,
                message = "피드가 존재하지 않습니다.",
            )
        }

        val comment = commentRepository.findByIdOrNull(commentId)

        if (comment == null) {
            throw NotFoundException(
                errorCode = ErrorCode.ROW_NOT_FOUND,
                message = "댓글이 존재하지 않습니다.",
            )
        }

        if (comment.writer != user.id) {
            throw UnauthorizedException(
                errorCode = ErrorCode.NOT_AUTHORIZED,
                message = "댓글 작성자가 아닙니다.",
            )
        }

        comment.content = req.comment

        return CommentResult(
            comment = comment,
            user = user
        )
    }

    @Transactional
    fun deleteFeedComment(userId: Long, feedId: Long, commentId: Long) {
        val user = userRepository.findByIdOrNull(userId)

        if (user == null) {
            throw UnauthorizedException(
                errorCode = ErrorCode.USER_NOT_FOUND,
                message = "유저가 존재하지 않습니다.",
            )
        }

        val feed = feedRepository.findByIdOrNull(feedId)

        if (feed == null) {
            throw NotFoundException(
                errorCode = ErrorCode.ROW_NOT_FOUND,
                message = "피드가 존재하지 않습니다.",
            )
        }

        val comment = commentRepository.findByIdOrNull(commentId)

        if (comment == null) {
            throw NotFoundException(
                errorCode = ErrorCode.ROW_NOT_FOUND,
                message = "댓글이 존재하지 않습니다.",
            )
        }

        if (comment.writer != user.id) {
            throw UnauthorizedException(
                errorCode = ErrorCode.NOT_AUTHORIZED,
                message = "댓글 작성자가 아닙니다.",
            )
        }

        commentRepository.delete(comment)
    }

    @Transactional
    fun createFeedLike(userId: Long, feedId: Long): Like {
        val user = userRepository.findByIdOrNull(userId)

        if (user == null) {
            throw UnauthorizedException(
                errorCode = ErrorCode.USER_NOT_FOUND,
                message = "유저가 존재하지 않습니다.",
            )
        }

        val feed = feedRepository.findByIdOrNull(feedId)

        if (feed == null) {
            throw NotFoundException(
                errorCode = ErrorCode.ROW_NOT_FOUND,
                message = "피드가 존재하지 않습니다.",
            )
        }

        val like = likeRepository.save(
            Like(feedId = feed.id!!, userId = user.id!!)
        )

        return like
    }

    @Transactional
    fun deleteFeedLike(userId: Long, feedId: Long) {
        val user = userRepository.findByIdOrNull(userId)

        if (user == null) {
            throw UnauthorizedException(
                errorCode = ErrorCode.USER_NOT_FOUND,
                message = "유저가 존재하지 않습니다.",
            )
        }

        val feed = feedRepository.findByIdOrNull(feedId)

        if (feed == null) {
            throw NotFoundException(
                errorCode = ErrorCode.ROW_NOT_FOUND,
                message = "피드가 존재하지 않습니다.",
            )
        }

        likeRepository.deleteByFeedIdAndUserId(feed.id!!, user.id!!)
    }

    @Transactional(readOnly = true)
    fun getFeedLikeList(userId: Long, feedId: Long, cursor: Long?, size: Int): LikeListResult {
        val user = userRepository.findByIdOrNull(userId)

        if (user == null) {
            throw UnauthorizedException(
                errorCode = ErrorCode.USER_NOT_FOUND,
                message = "유저가 존재하지 않습니다.",
            )
        }

        val feed = feedRepository.findByIdOrNull(feedId)

        if (feed == null) {
            throw NotFoundException(
                errorCode = ErrorCode.ROW_NOT_FOUND,
                message = "피드가 존재하지 않습니다.",
            )
        }

        val likes = if (cursor == null) {
            likeRepository.findByFeedIdOrderByIdDesc(feedId, size + 1)
        } else {
            likeRepository.findByFeedIdAndIdLessThanOrderByIdDesc(feedId, cursor, size + 1)
        }

        val hasNext = likes.size > size
        val items = if (hasNext) likes.dropLast(1) else likes
        val nextCursor = if (hasNext) items.last().id else null

        // 좋아요 누른 유저들의 User 정보 조회
        val userIds = items.map { it.userId }
        val users = userRepository.findByIdIn(userIds)
        val userMap = users.associateBy { it.id!! }

        val likeResults = items.map { like ->
            LikeResult(
                like = like,
                user = userMap[like.userId]!!
            )
        }

        return LikeListResult(
            likeList = likeResults,
            nextCursor = nextCursor
        )
    }
}