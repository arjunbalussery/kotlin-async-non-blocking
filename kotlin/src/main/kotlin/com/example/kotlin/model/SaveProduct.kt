package com.example.kotlin.model

import com.fasterxml.jackson.annotation.JsonProperty

data class SaveProduct(@JsonProperty("name") val name: String)
