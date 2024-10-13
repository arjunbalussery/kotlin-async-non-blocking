package com.example.kotlin.config

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component
import org.springframework.transaction.ReactiveTransaction
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

@Component
class R2dbcTransactionManager(
    private val transactionalOperator: TransactionalOperator
):TransactionManager {
    override suspend fun <T> transaction(block: TransactionBlock<T>): T {
        val mdcContext = Dispatchers.IO
        return transactionalOperator.executeAndAwait { transaction: ReactiveTransaction ->
            withContext(mdcContext) {
                kotlin.runCatching {
                    block()
                }.onFailure {
                    transaction.setRollbackOnly()
                }.getOrThrow()
            }
        }
    }

}

interface TransactionManager {
    suspend fun <T> transaction(block: TransactionBlock<T>): T
}

typealias TransactionBlock<T> = suspend () -> T