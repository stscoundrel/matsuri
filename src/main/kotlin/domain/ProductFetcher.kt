package io.github.stscoundrel.matsuri.domain

interface ProductFetcher {
    val category: String

    fun fetchProducts(): List<Product>
}