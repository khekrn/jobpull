package com.khekrn.jobpull.redis

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class RedisConfig {
    @Value("\${redis.hostname}")
    private val redisHostName: String? = null

    @Value("\${redis.port}")
    private val redisPort: String? = null
    private val logger = LoggerFactory.getLogger(RedisConfig::class.java)
    @Bean
    fun client(): RedissonClient {
        val config = Config()
        val address = "$REDIS_BASE_URL$redisHostName:$redisPort"
        logger.info("Connecting to redis end-point: {}", address)
        config.useSingleServer().address = address
        try {
            return Redisson.create(config)
        } catch (e: Exception) {
            logger.error("Unable to connect to cache with error: {}", e.message)
            throw e
        }
    }

    companion object {
        const val REDIS_BASE_URL = "redis://"
    }
}