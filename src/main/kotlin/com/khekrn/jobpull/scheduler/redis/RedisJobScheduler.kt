package com.khekrn.jobpull.scheduler.redis

import com.khekrn.jobpull.domain.JobRepository
import com.khekrn.jobpull.process.impl.AsyncJobProcessor
import com.khekrn.jobpull.scheduler.JobScheduler
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.future.await
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.redisson.api.RLockAsync
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

@Component
class RedisJobScheduler(
    private val redissonClient: RedissonClient,
    private val jobsRepository: JobRepository,
    private val asyncJobProcessor: AsyncJobProcessor
) : JobScheduler, CoroutineScope {

    private val logger = LoggerFactory.getLogger(RedisJobScheduler::class.java)

    private val defaultWaitTime = 5000L

    private val mx = Mutex()

    private var canContinue = true

    private val supervisorJob = SupervisorJob()

    private val customDispatcher = Executors.newFixedThreadPool(5)

    override val coroutineContext: CoroutineContext
        get() = customDispatcher.asCoroutineDispatcher() + supervisorJob


    override fun startScheduler() {
        runBlocking {
            launch {
                logger.info("In poll")
                while (isActive && canContinue) {
                    if (!asyncJobProcessor.isFull()) {
                        //logger.info("Job Queue is not full, polling the jobs")
                        val jobs = poll(5)
                        //logger.info("Total Jobs = {}", jobs.size)
                        if (jobs.isNotEmpty()) {
                            val jobEntityList = jobsRepository.findAllById(jobs).toList()
                            for (jobEntity in jobEntityList) {
                                asyncJobProcessor.executeJob(jobEntity)
                            }
                        }
                    } else {
                        logger.info("Job queue is full, Retrying")
                    }
                    delay(defaultWaitTime)
                }
                //logger.info("Return from poll")
            }
        }

    }

    fun stop() {
        logger.info("In stop")

        supervisorJob.cancel()
        stopScheduler()

        redissonClient.shutdown()

        logger.info("Return from stop")
    }

    private suspend fun poll(batch: Int): List<Long> {
        var result: List<Long> = emptyList()
        try {
            val lock: RLockAsync = redissonClient.getLock("lockJobs")
            val isLockFound = lock.tryLockAsync(60, TimeUnit.SECONDS).toCompletableFuture().await()
            if (isLockFound) {
                try {
                    val rQueue = redissonClient.getQueue<Long>("jobs")
                    result = rQueue.pollAsync(batch).toCompletableFuture().await()
                } finally {
                    lock.unlockAsync()
                }
            }
        } catch (ex: Exception) {
            logger.error("Error while polling from redis queue = {}", ex.toString())
            throw ex
        }
        return result
    }

    override fun stopScheduler() {
        runBlocking {
            mx.withLock {
                canContinue = false
            }
        }
    }


}