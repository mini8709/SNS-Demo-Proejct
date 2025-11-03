package com.youngmin.sns.domain.entity

import jakarta.persistence.*

@Entity
@Table(
    name = "follow",
    indexes = [
        Index(name = "idx_follower_id", columnList = "followerId, id DESC"),
        Index(name = "idx_following_id", columnList = "followingId, id DESC"),
        Index(name = "idx_follower_following", columnList = "followerId, followingId", unique = true)
    ]
)
class Follow (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var followerId: Long,

    @Column(nullable = false)
    var followingId: Long,
)