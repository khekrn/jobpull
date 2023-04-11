package com.khekrn.jobpull.domain

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.node.ObjectNode

@JsonInclude(JsonInclude.Include.NON_NULL)
data class JobRequest(
    @JsonProperty("jobName")
    val jobName: String,
    @JsonProperty("jobType")
    val jobType: String,
    @JsonProperty("data")
    val data: ObjectNode
)

data class JobPollDTO(val startPolling: Boolean)

