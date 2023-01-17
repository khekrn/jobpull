package com.khekrn.jobpull.api

import com.khekrn.jobpull.domain.JobDTO
import com.khekrn.jobpull.domain.JobEntity
import com.khekrn.jobpull.domain.JobRepository
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController(value = "Job")
@RequestMapping("/api/v1")
class JobController(private val jobRepository: JobRepository) {

    private val logger = LoggerFactory.getLogger(JobController::class.java)

    @GetMapping("/healthCheck")
    fun healthCheck(): ResponseEntity<String>{
        return ResponseEntity.ok("Service is up and running")
    }

    @PostMapping("/job", produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun addJob(job: JobDTO): ResponseEntity<String> {
        logger.info("In addJob")
        logger.debug("Received Data = {}", job)
        var jobEntity = JobEntity(
            jobName = job.jobName,
            jobType = job.jobType,
            payload = job.data,
            createdAt = LocalDateTime.now()
        )

        jobEntity = jobRepository.save(jobEntity)
        logger.debug("After saving the job entity = {}", jobEntity)
        return ResponseEntity.ok("Job Created")
    }

    @GetMapping("/job/{jobId}")
    suspend fun getJob(@PathVariable("jobId") id: Long): ResponseEntity<Any>{
        logger.info("In getJob")
        logger.debug("Fetching job details for {}", id)

        val jobEntity = jobRepository.findById(id)
        logger.info("Return from getJob")
        return if (jobEntity == null){
            ResponseEntity.badRequest().body("Invalid id is provided")
        }else{
            ResponseEntity.ok(jobEntity)
        }
    }

}