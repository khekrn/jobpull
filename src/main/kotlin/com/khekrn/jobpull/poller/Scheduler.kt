package com.khekrn.jobpull.poller

import com.khekrn.jobpull.domain.JobRepository
import com.khekrn.jobpull.process.AsyncJobDelegator
import com.khekrn.jobpull.process.JobContext
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.coroutines.CoroutineContext
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Component
class Scheduler(
    private val jobContext: JobContext,
    private val asyncJobDelegator: AsyncJobDelegator,
    private val jobRepository: JobRepository
) {

    private val logger = LoggerFactory.getLogger(Scheduler::class.java)
    private val mutex = Mutex()

    private var isPollingStarted = true

    fun runScheduler(): Job {
        logger.info("In pollJobs")
        return CoroutineScope(Dispatchers.Default).launch{
            try {
                while (isActive && isPollingStarted) {
                    logger.info("Current Job Queue Count = {}", jobContext.jobCount())
                    if (jobContext.isFull()) {
                        logger.info("Job Queue is full, Hence Sleeping the scheduler for next 2 mins")
                        delay(5.toDuration(DurationUnit.MINUTES))
                    } else {
                        val jobEntities = jobRepository.pollJobs(10)
                        if (jobEntities.isEmpty()) {
                            logger.info("No Job Found, Hence Sleeping the scheduler for next 2 mins")
                            delay(1.toDuration(DurationUnit.MINUTES))
                        } else {
                            logger.info("Jobs found and total size = {}", jobEntities.size)
                            asyncJobDelegator.addJob(jobEntities)
                        }
                    }
                }
            } catch (ex: Exception) {
                logger.error("Problem while polling database = {}", ex.stackTraceToString())
            }
        }
    }

    suspend fun stopPolling() {
        mutex.withLock {
            isPollingStarted = false
        }
    }

    suspend fun startPolling() {
        mutex.withLock {
            isPollingStarted = true
        }
    }

    suspend fun isPollingStarted() = isPollingStarted

}