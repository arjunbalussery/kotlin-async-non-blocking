package com.example.kotlin.repository

import com.example.generated.Tables.PRODUCT
import com.example.generated.tables.records.ProductRecord
import com.example.kotlin.config.R2dbcTransactionManager
import com.example.kotlin.model.Product
import jakarta.transaction.TransactionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.future.asDeferred
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.annotation.ComponentScan
import org.springframework.stereotype.Repository
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.util.*
import java.util.concurrent.CompletionStage

@Repository
@ComponentScan("com.example.generated.tables.daos")
class ProductRepository(
    private val jooq: DSLContext,
    private val transactionManager: R2dbcTransactionManager
) {

    suspend fun findAll(): Flow<Product> = jooq
        .selectFrom(PRODUCT)
        .asFlow()
        .map { Product(it.id!!, it.name!!) }

    suspend fun save(product: Product): Flow<UUID> = transactionManager.transaction {
        jooq.insertInto(PRODUCT)
            .columns(PRODUCT.ID, PRODUCT.NAME)
            .values(product.id, product.name)
            .asFlow()
            .map{ product.id }
    }


    @Cacheable("yourCacheName", key = "#id")
    suspend fun get(id: UUID): Product? = jooq
        .selectFrom(PRODUCT)
        .where(PRODUCT.ID.eq(id))
        .awaitFirstOrNull()
        ?.let { Product(it.id!!, it.name!!) }
}