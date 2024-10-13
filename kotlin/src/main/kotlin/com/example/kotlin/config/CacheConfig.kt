package com.example.kotlin.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit


@EnableCaching
@Configuration
class CacheConfig {
    @Bean
    fun cacheManager(): CacheManager? {
        val cacheManager = CaffeineCacheManager()
        caffeineCacheBuilder()?.let { cacheManager.setCaffeine(it) }
        return cacheManager
    }

    private fun caffeineCacheBuilder(): Caffeine<Any?, Any?>? {
        return com.github.benmanes.caffeine.cache.Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES) // Set cache expiration time
            .maximumSize(100) // Set maximum cache size
            .recordStats() // Enable cache statistics (optional)
    }
}