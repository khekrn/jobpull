package com.khekrn.jobpull.api

import com.khekrn.jobpull.domain.JobRequest
import com.khekrn.jobpull.domain.JobEntity
import com.khekrn.jobpull.domain.JobRepository
import com.khekrn.jobpull.json.JsonUtils
import com.khekrn.jobpull.poller.Scheduler
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController(value = "Job")
@RequestMapping("/api/v1")
class JobController(
    private val scheduler: Scheduler,
    private val jobRepository: JobRepository
) {

    private val logger = LoggerFactory.getLogger(JobController::class.java)

    @GetMapping("/healthCheck")
    fun healthCheck(): ResponseEntity<String> {
        return ResponseEntity.ok("Service is up and running")
    }

    @PostMapping("/job", produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun addJob(@RequestBody job: JobRequest): ResponseEntity<String> {
        logger.info("In addJob")
        logger.debug("Received Data = {}", job)

        val payload = JsonUtils.toJson(job.data)
        var jobEntity = JobEntity(
            jobName = job.jobName,
            jobType = job.jobType,
            payload = payload,
            variables = payload,
            status = "ENQUEUED",
            createdAt = LocalDateTime.now()
        )

        jobEntity = jobRepository.save(jobEntity)
        logger.debug("After saving the job entity = {}", jobEntity)
        return ResponseEntity.ok("Job Created")
    }

    @PostMapping(
        "/job/stop",
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    suspend fun stopPoll(): ResponseEntity<String> {
        logger.info("In jobPoll")

        scheduler.stopPolling()
        return ResponseEntity.ok("Stopping the polling now")
    }


    @GetMapping("/job/{jobId}")
    suspend fun getJob(@PathVariable("jobId") id: Long): ResponseEntity<Any> {
        logger.info("In getJob")
        logger.debug("Fetching job details for {}", id)

        val jobEntity = jobRepository.findById(id)
        logger.info("Return from getJob")
        return if (jobEntity == null) {
            ResponseEntity.badRequest().body("Invalid id is provided")
        } else {
            ResponseEntity.ok(jobEntity)
        }
    }

}