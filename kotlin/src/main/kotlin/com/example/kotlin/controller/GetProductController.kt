package com.example.kotlin.controller

import com.example.kotlin.model.Product
import com.example.kotlin.service.ProductService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class GetProductController(private val service: ProductService) {
    @GetMapping("/api/v1/{id}")
    suspend fun get(@PathVariable id: UUID): ResponseEntity<Product> = service
        .get(id)
        .let { ResponseEntity(it, HttpStatus.OK) }

}
