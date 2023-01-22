package com.khekrn.jobpull.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.khekrn.jobpull.domain.JobDTO
import com.khekrn.jobpull.domain.JobEntity
import com.khekrn.jobpull.domain.JobRepository
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController(value = "Job")
@RequestMapping("/api/v1")
class JobController(
    private val jobRepository: JobRepository
    //private val redissonClient: RedissonClient
) {

    private val logger = LoggerFactory.getLogger(JobController::class.java)

    private val objectMapper = ObjectMapper()

    @GetMapping("/healthCheck")
    fun healthCheck(): ResponseEntity<String> {
        return ResponseEntity.ok("Service is up and running")
    }

    @PostMapping("/job", produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun addJob(@RequestBody job: JobDTO): ResponseEntity<String> {
        logger.info("In addJob")
        logger.info("Received Data = {}", job)
        var jobEntity = JobEntity(
            jobName = job.jobName,
            jobType = job.jobType,
            payload = objectMapper.writeValueAsString(job.payload),
            retries = 0,
            status = "ENQUEUED",
            createdAt = LocalDateTime.now()
        )

        jobEntity = jobRepository.save(jobEntity)

       /* val rQueue = redissonClient.getQueue<JobEntity>("jobs")
        val result = rQueue.addAsync(jobEntity).toCompletableFuture().await()
        logger.info("After adding the values in redis = {}", result)*/

        logger.info("After saving the job entity = {}", jobEntity)
        return ResponseEntity.ok("Job Created with id "+jobEntity.id)
    }

    @GetMapping("/job/{jobId}")
    suspend fun getJob(@PathVariable("jobId") id: Long): ResponseEntity<Any> {
        logger.info("In getJob")
        logger.info("Fetching job details for {}", id)

        /*val rQueue = redissonClient.getQueue<JobEntity>("jobs")
        val entity = rQueue.pollAsync().toCompletableFuture().await()
        logger.info("After fetching it from redis = {}", entity)*/

        val jobEntity = jobRepository.findById(id)
        logger.info("Return from getJob")
        return if (jobEntity == null) {
            ResponseEntity.badRequest().body("Invalid id is provided")
        } else {
            ResponseEntity.ok(jobEntity)
        }
    }

}