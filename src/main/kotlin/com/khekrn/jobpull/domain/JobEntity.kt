package com.khekrn.jobpull.domain

import com.fasterxml.jackson.annotation.JsonRawValue
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime


@Table(value = "job")
data class JobEntity(
    @Id
    var id: Long? = null,

    @Column(value = "name")
    var jobName: String,

    @Column(value = "type")
    var jobType: String,

    @JsonRawValue
    var payload: String,

    @JsonRawValue
    var variables: String,

    var status: String,

    @Column("created_at")
    var createdAt: LocalDateTime?,

    @Column("updated_at")
    var updatedAt: LocalDateTime? = LocalDateTime.now()
)
