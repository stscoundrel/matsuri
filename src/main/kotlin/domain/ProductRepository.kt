package io.github.stscoundrel.matsuri.domain

interface ProductRepository {
    fun find(
        category: String,
        id: String
    ): Product?

    fun save(product: Product)
}