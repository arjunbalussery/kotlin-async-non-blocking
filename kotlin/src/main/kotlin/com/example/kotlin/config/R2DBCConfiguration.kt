package com.example.kotlin.config

import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.pool.PoolingConnectionFactoryProvider.*
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import io.r2dbc.postgresql.PostgresqlConnectionFactoryProvider.*
import io.r2dbc.postgresql.client.SSLMode
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import io.r2dbc.spi.ConnectionFactoryOptions.*
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcDataAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.time.Duration


@Configuration
@Component
class R2DBCConfiguration(
    private val properties: R2dbcProperties,
    private val customR2dbcProperties: CustomR2dbcProperties
) {

   /* @Bean
    @Primary
    fun connectionFactory(properties: R2dbcProperties): ConnectionFactory {
        val options: MutableMap<String, String> = HashMap()
        return ConnectionFactories.get(
            ConnectionFactoryOptions.builder()
                .option(PROTOCOL, "postgresql")
                .option<String>(DRIVER, "pool")
                .option<String>(HOST, "postgres")
                .option<Int>(PORT, 5432)
                .option<String>(USER, "postgres")
                .option(PASSWORD, "pass123")
                .option<String>(DATABASE, "postgres")
                .option(MAX_SIZE, 20)
                .option(INITIAL_SIZE, 20)
                .option(SCHEMA, "public")
                .option(MAX_ACQUIRE_TIME, Duration.ofSeconds(30))
                .option(MAX_IDLE_TIME, Duration.ofSeconds(30))
                .option(SSL_MODE, SSLMode.DISABLE)
                .build()
        )
    }*/

    @Bean
    @Primary
    fun connectionFactory(): ConnectionFactory {
        val connectionFactory = PostgresqlConnectionFactory(
            PostgresqlConnectionConfiguration.builder()
                .host(customR2dbcProperties.host)
                .port(customR2dbcProperties.port)
                .username(properties.username)
                .password(properties.password)
                .sslMode(SSLMode.DISABLE)
                .build()
        )
        val poolConfiguration = ConnectionPoolConfiguration
            .builder(connectionFactory)
            .maxSize(20)
            .initialSize(20)
            .maxAcquireTime(Duration.ofSeconds(30))
            .maxIdleTime(Duration.ofSeconds(30))
            .build()
        return ConnectionPool(poolConfiguration)
    }
}

@ConfigurationProperties("spring.r2dbc")
@Component
class CustomR2dbcProperties {
    lateinit var host: String
    lateinit var database: String
    var port:Int=5432
}
