package com.youngmin.sns.config.exceptions

import java.lang.RuntimeException

class ConflictException(
    val errorCode: ErrorCode,
    override val message: String
) : RuntimeException(message)