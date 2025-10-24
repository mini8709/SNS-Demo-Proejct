package com.youngmin.sns.config.exceptions

import java.lang.RuntimeException

class BadRequestException(
    val errorCode: ErrorCode,
    override val message: String
) : RuntimeException(message)