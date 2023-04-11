package com.khekrn.jobpull.config

import com.khekrn.jobpull.poller.Scheduler
import com.khekrn.jobpull.process.AsyncJobDelegator
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import org.flywaydb.core.Flyway
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment


@Configuration
class JobQueueConfig(
    private val env: Environment,
    private val scheduler: Scheduler,
    private val asyncJobDelegator: AsyncJobDelegator
) {

    private lateinit var job: Job

    @PostConstruct
    fun initialize() {
        val flyway = Flyway.configure()
            .dataSource(
                env.getProperty("flyway.jdbc.url"),
                env.getProperty("spring.r2dbc.username"),
                env.getProperty("spring.r2dbc.password")
            ).load()
        flyway.migrate()
        asyncJobDelegator.start(512)
        //job = scheduler.runScheduler()
    }

    @PreDestroy
    fun shutDown() {
        asyncJobDelegator.stop()
        /*runBlocking {
            job.cancelAndJoin()
        }*/
    }
}