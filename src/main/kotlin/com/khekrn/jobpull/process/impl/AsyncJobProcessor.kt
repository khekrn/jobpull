package com.khekrn.jobpull.process.impl

import com.khekrn.jobpull.domain.JobEntity
import com.khekrn.jobpull.domain.JobRepository
import com.khekrn.jobpull.executor.JobExecutor
import com.khekrn.jobpull.process.JobProcessor
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.coroutines.CoroutineContext

@Component
class AsyncJobProcessor(
    private val jobExecutor: JobExecutor,
    private val redissonClient: RedissonClient,
    private val jobsRepository: JobRepository
) : JobProcessor, CoroutineScope {

    private val logger = LoggerFactory.getLogger(AsyncJobProcessor::class.java)

    private val supervisorJob = SupervisorJob()

    private val channel = Channel<Deferred<JobEntity>>()

    private var queueCount = 0

    private val mx = Mutex()

    private val maxWorkers: Int = 5

    private val halfWorkers = 2

    private var canContinue = true

    private val defaultWaitTime = 5L

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + supervisorJob

    fun start() = launch {
        logger.info("In Start")
        repeat(maxWorkers) {
            launch {
                logger.info("Launching worker $it")
                for (job in channel) {
                    try {
                        jobExecutor.runJob(job.await())
                    } catch (ex: Exception) {
                        logger.error(
                            "Error: Problem while processing job = {} - {}",
                            job,
                            ex.toString()
                        )
                    } finally {
                        mx.withLock {
                            queueCount--
                        }
                    }
                }

            }
        }
        logger.info("Return from start")
    }

    fun stop() {
        logger.info("In stop")

        supervisorJob.cancel()

        logger.info("Return from stop")
    }

    override suspend fun executeJob(jobEntity: JobEntity) {
        coroutineScope {
            launch {
                logger.info("In executeJob")
                try {
                    channel.send(async { jobEntity })
                } catch (ex: Exception) {
                    logger.error("Problem while sending data to the channel = {}", ex.toString())
                    throw ex
                } finally {
                    mx.withLock {
                        queueCount++
                    }
                }
                logger.info("Return from executeJob")
            }
        }
    }

    override suspend fun isFull(): Boolean {
        logger.info("Current queue size = {}", queueCount)
        return (queueCount >= halfWorkers)
    }
}