package com.youngmin.sns.domain.entity

import jakarta.persistence.*

@Entity
@Table(
    name = "likes",
    indexes = [
        Index(name = "idx_feedId_id", columnList = "feedId, id DESC"),
        Index(name = "idx_feedId_userId", columnList = "feedId, userId", unique = true)
    ]
)
class Like (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var feedId: Long,

    @Column(nullable = false)
    var userId: Long,
)