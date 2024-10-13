package com.example.kotlin.controller

import com.example.kotlin.model.Product
import com.example.kotlin.service.ProductService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class FindAllProductController(private val service: ProductService) {

    @GetMapping("/api/v1")
    suspend fun findAll() : List<Product> = service
            .findAll()
}
