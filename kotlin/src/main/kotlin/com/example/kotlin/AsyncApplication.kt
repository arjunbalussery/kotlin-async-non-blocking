package com.example.kotlin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@ComponentScan
@EnableTransactionManagement

class AsyncApplication

fun main(args: Array<String>) {
	runApplication<AsyncApplication>(*args)
}
