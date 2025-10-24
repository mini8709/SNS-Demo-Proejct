package com.youngmin.sns.config.exceptions

import java.time.LocalDateTime

data class ErrorResponse(
    val status: Int,
    val errorCode: Int,
    val message: String,
    val path: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
)
