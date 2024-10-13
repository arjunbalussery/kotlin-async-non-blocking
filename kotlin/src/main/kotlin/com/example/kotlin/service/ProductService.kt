package com.example.kotlin.service

import com.example.generated.tables.records.ProductRecord
import com.example.kotlin.model.Product
import com.example.kotlin.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Mono
import java.util.*

@Service
class ProductService(
    private val repository: ProductRepository
    ) {

    suspend fun findAll(): List<Product> = repository
        .findAll()
        .toList()


    suspend fun save(product: Product): Flow<UUID> {
        println("**************service: ${product.id} ***********")
        return repository
            .save(product)
    }

    @Cacheable("yourCacheName", key = "#id")
    suspend fun get(id: UUID): Product? = repository
        .get(id)
}