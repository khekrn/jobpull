package com.khekrn.jobpull

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class JobpullApplication

fun main(args: Array<String>) {
	runApplication<JobpullApplication>(*args)
}
