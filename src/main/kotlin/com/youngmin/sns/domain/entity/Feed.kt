package com.youngmin.sns.domain.entity

import jakarta.persistence.*

@Entity
@Table(
    name = "feed",
    indexes = [
        Index(name = "idx_writer_id", columnList = "writer, id DESC")
    ]
)
class Feed (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var writer: Long,

    @Column(nullable = false)
    var content: String,
) : BaseTimeEntity()