package com.example.kotlin.repository

import com.example.kotlin.config.R2dbcTransactionManager
import com.example.kotlin.model.Product
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.jooq.DSLContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait

import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

import java.util.UUID

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class ProductRepositoryIntegrationTest {

    companion object {
        @Container
        val postgresContainer = PostgreSQLContainer<Nothing>("postgres:15-alpine")
            .apply {
                waitingFor(Wait.forListeningPorts())
            }



        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.r2dbc.url") {
                postgresContainer.jdbcUrl.replace("jdbc","r2dbc")
            }
            registry.add("spring.r2dbc.username", postgresContainer::getUsername)
            registry.add("spring.r2dbc.password", postgresContainer::getPassword)
            registry.add("spring.r2dbc.host"){"localhost"}
            registry.add("spring.r2dbc.port"){postgresContainer.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT)}
            registry.add("spring.r2dbc.database"){postgresContainer.databaseName}
            registry.add("spring.flyway.url")
            {
                postgresContainer.jdbcUrl
            }
            registry.add("spring.flyway.user", postgresContainer::getUsername)
            registry.add("spring.flyway.password", postgresContainer::getPassword)
        }
    }

    @Autowired
    lateinit var productRepository: ProductRepository

    @Autowired
    lateinit var jooq: DSLContext

    @Autowired
    lateinit var transactionManager: R2dbcTransactionManager


    @Test
    fun `should save and find all products`() = runBlocking {
        // Given
        val product1 = Product(UUID.randomUUID(), "Product 1")
        val product2 = Product(UUID.randomUUID(), "Product 2")

        // When
        productRepository.save(product1).toList()
        productRepository.save(product2).toList()
        val products = productRepository.findAll().toList()

        // Then
        assertEquals(2, products.size)
        assertEquals(product1, products[0])
        assertEquals(product2, products[1])
    }

    @Test
    fun `should get product by id`() = runBlocking {
        // Given
        val product = Product(UUID.randomUUID(), "Product 1")
        productRepository.save(product).toList()

        // When
        val foundProduct = productRepository.get(product.id)

        // Then
        assertEquals(product, foundProduct)
    }
}