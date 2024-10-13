package com.example.kotlin.model

import java.util.UUID

data class Product(val id: UUID = UUID.randomUUID(), val name: String)
