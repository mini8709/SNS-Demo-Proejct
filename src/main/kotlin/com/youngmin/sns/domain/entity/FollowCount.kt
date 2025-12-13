package com.youngmin.sns.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(
    name = "follow_count",
)
class FollowCount (
    @Id
    val id: Long,

    @Column(nullable = false)
    var followerCount: Long,

    @Column(nullable = false)
    var followingCount: Long,
)
