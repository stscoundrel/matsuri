package io.github.stscoundrel.matsuri.application

interface TransactionRunner {
    fun <T> transaction(block: () -> T): T
}
