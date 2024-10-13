package com.example.kotlin.config

import io.r2dbc.spi.ConnectionFactory
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.TransactionListenerProvider
import org.jooq.impl.DefaultConfiguration
import org.springframework.boot.autoconfigure.jooq.SpringTransactionProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.r2dbc.connection.TransactionAwareConnectionFactoryProxy
import org.springframework.stereotype.Component

@Configuration
@Component
class JooqConfig {
    @Bean
    fun dslContext(connectionFactory: ConnectionFactory): DSLContext {
        return org.jooq.impl.DSL.using(
            DefaultConfiguration()
                .set(TransactionAwareConnectionFactoryProxy(connectionFactory))
                .set(SQLDialect.POSTGRES)
        )
            .dsl()
    }
}