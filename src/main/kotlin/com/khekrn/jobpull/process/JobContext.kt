package com.khekrn.jobpull.process

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.stereotype.Component


@Component
class JobContext {

    private val mutex = Mutex()
    private var queueCount = 0

    private val maxCount = 64

    suspend fun increment(){
        mutex.withLock {
            queueCount += 1
        }
    }

    suspend fun decrement(){
        mutex.withLock {
            queueCount -= 1
        }
    }

    fun isFull() = queueCount >= maxCount

    fun jobCount() = queueCount

}