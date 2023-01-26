package com.khekrn.jobpull.process

import com.khekrn.jobpull.domain.JobEntity

interface JobProcessor {

    suspend fun executeJob(jobEntity: JobEntity)

    suspend fun isFull(): Boolean
}