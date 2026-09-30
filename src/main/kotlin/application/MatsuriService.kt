package io.github.stscoundrel.matsuri.application

import io.github.stscoundrel.matsuri.domain.ProductFetcher
import io.github.stscoundrel.matsuri.domain.ProductTrackingResult

class MatsuriService(
    private val fetchers: List<ProductFetcher>,
    private val tracker: ProductTracker
) {

    fun run(): List<ProductTrackingResult> {
        return fetchers.map { fetcher ->
            tracker.run(fetcher)
        }
    }
}