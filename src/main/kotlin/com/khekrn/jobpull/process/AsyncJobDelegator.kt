package com.khekrn.jobpull.process

import com.khekrn.jobpull.domain.JobEntity
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.coroutines.CoroutineContext

@Component
class AsyncJobDelegator(private val jobContext: JobContext,
                        private val jobProcess: JobProcess
) : CoroutineScope {

    private val logger = LoggerFactory.getLogger(AsyncJobDelegator::class.java)

    private val supervisorJob = SupervisorJob()

    private val channel = Channel<Deferred<JobEntity>>()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + supervisorJob

    fun start(maxWorkers: Int) = launch {
        logger.info("In Start")
        repeat(maxWorkers) {
            launch {
                logger.info("Launching worker $it")
                for (workflowRequestDeferred in channel) {
                    try {
                        jobProcess.run(workflowRequestDeferred.await())
                    } catch (ex: Exception) {
                        logger.error(
                            "Error: Problem while processing job = {} - {}",
                            workflowRequestDeferred,
                            ex.printStackTrace()
                        )
                    }
                }

            }
        }


        logger.info("Return from start")
    }

    fun stop() {
        logger.info("In stop")

        supervisorJob.cancel()

        logger.info("Return from stop")
    }

    suspend fun addJob(jobList: List<JobEntity>) {
        coroutineScope {
            launch {
                logger.info("In addJob")
                for (job in jobList) {
                        try {
                            channel.send(async { job })
                            jobContext.increment()
                        } catch (ex: Exception) {
                            logger.error("Problem while sending data to the channel = {} - {}", job, ex.toString())
                            throw ex
                        }
                }
                logger.info("Return from addJob")
            }
        }
    }
}