package com.khekrn.jobpull.process

import com.khekrn.jobpull.domain.History
import com.khekrn.jobpull.domain.HistoryRepository
import com.khekrn.jobpull.domain.JobEntity
import com.khekrn.jobpull.domain.JobRepository
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Component
class JobProcess(
    private val jobContext: JobContext,
    private val jobRepository: JobRepository,
    private val historyRepository: HistoryRepository,
    @Value("\${service.name}") private val name: String
) {

    private val logger = LoggerFactory.getLogger(JobProcess::class.java)

    suspend fun run(job: JobEntity) {
        logger.info("In run")
        logger.info("Executing = {}", job)

        delay(10.toDuration(DurationUnit.SECONDS))
        logger.info("Execution is completed for {}", job)

        var history = History(
            jobId = job.id,
            jobName = job.jobName,
            jobType = job.jobType,
            status = "SUCCESS",
            serviceName = name,
            createdAt = LocalDateTime.now()
        )

        history = historyRepository.save(history)
        logger.info("After saving in the history table = {}", history)

        jobRepository.delete(job)

        jobContext.decrement()

        logger.info("Return from run")
    }
}