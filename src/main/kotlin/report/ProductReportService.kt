package io.github.stscoundrel.matsuri.report

import io.github.stscoundrel.matsuri.domain.ProductTrackingResult

class ProductReportService {

    fun create(
        results: List<ProductTrackingResult>
    ): ProductReport {
        return ProductReport(
            results = results.flatMap { it.newImages }
        )
    }
}