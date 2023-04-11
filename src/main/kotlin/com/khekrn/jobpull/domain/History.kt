package com.khekrn.jobpull.domain

import com.fasterxml.jackson.annotation.JsonRawValue
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(value = "job_history")
data class History(
    @Id
    var id: Long? = null,

    @Column(value = "job_id")
    var jobId: Long? = null,

    @Column(value = "name")
    var jobName: String,

    @Column(value = "type")
    var jobType: String,

    var status: String,

    @Column(value = "service_name")
    var serviceName: String,

    @Column("created_at")
    var createdAt: LocalDateTime?,

    @Column("updated_at")
    var updatedAt: LocalDateTime? = LocalDateTime.now()
)