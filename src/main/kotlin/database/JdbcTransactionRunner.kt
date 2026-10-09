package io.github.stscoundrel.matsuri.database

import io.github.stscoundrel.matsuri.application.TransactionRunner
import java.sql.Connection

class JdbcTransactionRunner(
    private val connection: Connection
) : TransactionRunner {

    override fun <T> transaction(block: () -> T): T {
        check(connection.autoCommit) { "A transaction is already active" }
        connection.autoCommit = false

        var failure: Throwable? = null
        try {
            val result = block()
            connection.commit()
            return result
        } catch (error: Throwable) {
            failure = error
            try {
                connection.rollback()
            } catch (rollbackError: Throwable) {
                error.addSuppressed(rollbackError)
            }
            throw error
        } finally {
            try {
                connection.autoCommit = true
            } catch (restoreError: Throwable) {
                if (failure != null) {
                    failure.addSuppressed(restoreError)
                } else {
                    throw restoreError
                }
            }
        }
    }
}
