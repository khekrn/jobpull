package com.khekrn.jobpull.domain

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface JobRepository : CoroutineCrudRepository<JobEntity, Long> {

    @Query(
        "with updated_jobs as (update job set status='PROCESSING' where id IN " +
                "(select id from job e where status = 'ENQUEUED' order by created_at FOR UPDATE SKIP LOCKED " +
                "LIMIT $1) RETURNING *) select * from updated_jobs order by created_at"
    )
    suspend fun pollJobs(batch: Int): List<JobEntity>
}