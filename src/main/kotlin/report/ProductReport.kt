package io.github.stscoundrel.matsuri.report

import io.github.stscoundrel.matsuri.domain.Product

data class ProductReport(
    val results: List<Product>
)