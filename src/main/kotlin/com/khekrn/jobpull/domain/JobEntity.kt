package com.khekrn.jobpull.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime


@Table(value = "job")
data class JobEntity(
    @Id
    var id: Long? = null,

    @Column(value = "task_name")
    var jobName: String,

    @Column(value = "task_type")
    var jobType: String,

    var payload: String,

    @Column("created_at")
    var createdAt: LocalDateTime?,

    @Column("updated_at")
    var updatedAt: LocalDateTime? = LocalDateTime.now()
)
