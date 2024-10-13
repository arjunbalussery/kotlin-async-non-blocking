package com.example.kotlin.controller

import com.example.kotlin.model.Product
import com.example.kotlin.model.SaveProduct
import com.example.kotlin.service.ProductService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class SaveProductController(
    private val service: ProductService
) {
    @PostMapping("/api/v1")
    suspend fun save(@RequestBody saveProduct: SaveProduct): ResponseEntity<UUID> {
        val id = UUID.randomUUID()
        println("**************controller: $id***********")
        return service
            .save(Product(id,saveProduct.name))
            .let { ResponseEntity(it.first(), HttpStatus.CREATED) }
    }
}
