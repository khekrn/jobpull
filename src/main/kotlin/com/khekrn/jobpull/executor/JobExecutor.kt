package com.khekrn.jobpull.executor

import com.khekrn.jobpull.domain.JobEntity
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class JobExecutor {

    private val logger = LoggerFactory.getLogger(JobExecutor::class.java)

    private val random = Random(1000)

    suspend fun runJob(jobEntity: JobEntity){
        logger.info("Running Job = {}", jobEntity.id)
        val waitTime = random.nextLong(10000, 60000)
        logger.info("Wait time = {}", waitTime)
        delay(waitTime)
        logger.info("Job executed successfully = {}", jobEntity.id)
    }

}