package com.khekrn.jobpull.config

import com.khekrn.jobpull.process.impl.AsyncJobProcessor
import com.khekrn.jobpull.scheduler.redis.RedisJobScheduler
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener

@Configuration
class JobQueueConfig(
    private val asyncJobProcessor: AsyncJobProcessor,
    private val redisJobScheduler: RedisJobScheduler
) {

    @PostConstruct
    fun initialize() {
        asyncJobProcessor.start()
    }

    @EventListener(ApplicationReadyEvent::class)
    fun runScheduler() {
        println("Running after startup")
        redisJobScheduler.startScheduler()
    }

    @PreDestroy
    fun shutDown() {
        asyncJobProcessor.stop()
        redisJobScheduler.stop()
    }
}