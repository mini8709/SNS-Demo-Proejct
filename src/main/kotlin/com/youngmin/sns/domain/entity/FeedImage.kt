package com.youngmin.sns.domain.entity

import jakarta.persistence.*

@Entity
@Table(
    name = "feed_image",
    indexes = [
        Index(name = "idx_feedId", columnList = "feedId")
    ]
)
class FeedImage (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var feedId: Long,

    @Column(nullable = false)
    var imgSrc: String,
)
