package com.youngmin.sns

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class MySnsApplication

fun main(args: Array<String>) {
	runApplication<MySnsApplication>(*args)
}
