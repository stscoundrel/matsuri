package io.github.stscoundrel.matsuri.domain

data class ProductTrackingResult(
    val category: String,
    val newImages: List<Product>
)