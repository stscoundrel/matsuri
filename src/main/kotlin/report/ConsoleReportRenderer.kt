package io.github.stscoundrel.matsuri.report

class ConsoleReportRenderer {

    fun render(report: ProductReport) {
        if (report.results.isEmpty()) {
            println("No new product images.")
            return
        }

        println("New product images:")
        println()

        report.results.forEach { product ->
            println(product.name)
            println("  ${product.category}")
            println("  ${product.id}")
            println()
        }
    }
}