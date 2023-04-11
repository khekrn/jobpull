package com.khekrn.jobpull.domain

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface HistoryRepository: CoroutineCrudRepository<History, Long> {
}