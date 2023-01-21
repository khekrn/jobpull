package com.khekrn.jobpull.domain

import com.fasterxml.jackson.annotation.JsonProperty

data class JobDTO(
    @JsonProperty("jobName")
    val jobName: String,
    @JsonProperty("jobType")
    val jobType: String,

    @JsonProperty("data")
    val payload: MutableMap<String, Any>
)

