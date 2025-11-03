package com.youngmin.sns.domain.entity

import jakarta.persistence.*


@Entity
@Table(
    name = "users",
    indexes = [
        Index(name = "idx_email", columnList = "email")
    ]
)
class User (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var password: String,
)
