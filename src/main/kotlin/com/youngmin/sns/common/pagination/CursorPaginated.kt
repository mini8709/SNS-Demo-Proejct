package com.youngmin.sns.common.pagination

data class CursorPaginated<T> (
    val items: List<T>,
    val nextCursor: Long?
)
