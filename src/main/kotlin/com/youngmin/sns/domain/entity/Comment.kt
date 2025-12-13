package com.youngmin.sns.domain.entity

import jakarta.persistence.*

@Entity
@Table(
    name = "comment",
    indexes = [
        Index(name = "idx_feedId_id", columnList = "feedId, id DESC")
    ]
)
class Comment (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var feedId: Long,

    @Column(nullable = false)
    var writer: Long,

    @Column(nullable = false)
    var content: String,
) : BaseTimeEntity()