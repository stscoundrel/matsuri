package io.github.stscoundrel.matsuri.scraper

import com.microsoft.playwright.Playwright
import io.github.stscoundrel.matsuri.domain.Product
import io.github.stscoundrel.matsuri.domain.ProductFetcher

class AsetaloScraper(
    override val category: String
) : ProductFetcher {

    companion object {
        private const val BASE_URL = "https://www.asetalo.fi"

        private const val NO_IMAGE_PATH =
            "/admin/img/verkkokauppa/ei_kuvaa-iso.gif"
    }

    override fun fetchProducts(): List<Product> {
        val url = "$BASE_URL$category"

        Playwright.create().use { playwright ->
            playwright.chromium().launch().use { browser ->
                val page = browser.newPage()

                page.navigate(url)

                page.locator("#listaus").waitFor()

                val products =
                    page.locator("#listaus .tuotelistauskortti")

                val productCount = products.count()

                println("Found $productCount products")

                return (0 until productCount).map { i ->
                    val product = products.nth(i)

                    val productLink =
                        product.locator(".selaus_tuotenimi_iso a")

                    val href =
                        productLink.getAttribute("href") ?: ""

                    Product(
                        category = category,
                        id = extractProductId(href),
                        name = productLink
                            .textContent()
                            ?.trim()
                            ?: "",
                        hasImage = hasImage(product)
                    )
                }
            }
        }
    }

    private fun hasImage(
        product: com.microsoft.playwright.Locator
    ): Boolean {
        val image = product.locator(".selaus_kuva_iso img")

        val src = image.getAttribute("src") ?: return false

        return src != NO_IMAGE_PATH
    }

    private fun extractProductId(href: String): String {
        return href
            .trimEnd('/')
            .substringAfterLast('/')
    }
}