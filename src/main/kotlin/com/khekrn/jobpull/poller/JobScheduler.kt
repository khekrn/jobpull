package com.khekrn.jobpull.poller

import com.khekrn.jobpull.domain.JobRepository
import com.khekrn.jobpull.process.AsyncJobDelegator
import com.khekrn.jobpull.process.JobContext
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class JobScheduler(private val jobContext: JobContext,
    private val asyncJobDelegator: AsyncJobDelegator,
    private val jobRepository: JobRepository
) {

    private val logger = LoggerFactory.getLogger(JobScheduler::class.java)

    @Scheduled(fixedDelay = 30000)
    fun runScheduler(){
        logger.info("Running the scheduler, Job Size = {}", jobContext.jobCount())
        runBlocking {
            if (jobContext.isFull()) {
                logger.info("Job Queue is full, Hence Cannot process the job at this time !!")
            } else {
                val jobEntities = jobRepository.pollJobs(15)
                if (jobEntities.isEmpty()) {
                    logger.info("No Job Found, Hence Sleeping the scheduler for next 2 mins")
                } else {
                    logger.info("Jobs found and total size = {}", jobEntities.size)
                    asyncJobDelegator.addJob(jobEntities)
                }
            }
        }
        logger.info("Returning the scheduler")
    }
}