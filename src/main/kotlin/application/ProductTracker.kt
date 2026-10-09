package io.github.stscoundrel.matsuri.application

import io.github.stscoundrel.matsuri.domain.ProductFetcher
import io.github.stscoundrel.matsuri.domain.ProductRepository
import io.github.stscoundrel.matsuri.domain.ProductTrackingResult

class ProductTracker(
    private val repository: ProductRepository,
    private val transactionRunner: TransactionRunner
) {

    fun run(fetcher: ProductFetcher): ProductTrackingResult {
        val products = fetcher.fetchProducts()

        val newImages = transactionRunner.transaction {
            products.mapNotNull { product ->
                val previous = repository.find(
                    category = product.category,
                    id = product.id
                )

                repository.save(product)

                if (previous?.hasImage == false && product.hasImage) {
                    product
                } else {
                    null
                }
            }
        }

        return ProductTrackingResult(
            category = fetcher.category,
            newImages = newImages
        )
    }
}
