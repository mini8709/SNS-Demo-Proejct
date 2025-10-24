package com.youngmin.sns.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime

@Entity
@Table(name = "comment")
class Comment (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var feedId: Long,

    @Column(nullable = false)
    var writer: Long,

    @Column(nullable = false)
    var content: String,

    @Column(updatable = false)
    @CreatedDate
    var date: LocalDateTime? = null,
){

}