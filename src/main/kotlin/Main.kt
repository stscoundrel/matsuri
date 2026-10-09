package io.github.stscoundrel.matsuri

import io.github.stscoundrel.matsuri.application.MatsuriService
import io.github.stscoundrel.matsuri.application.ProductTracker
import io.github.stscoundrel.matsuri.database.Database
import io.github.stscoundrel.matsuri.database.JdbcTransactionRunner
import io.github.stscoundrel.matsuri.database.SqliteProductRepository
import io.github.stscoundrel.matsuri.report.ConsoleReportRenderer
import io.github.stscoundrel.matsuri.report.ProductReportService
import io.github.stscoundrel.matsuri.scraper.AsetaloScraper

fun main() {

    val fetchers = listOf(
        AsetaloScraper(
            "/aseet/kaytetyt-aseet/sotilaskivaarit-tt2/7852/"
        ),
        AsetaloScraper(
            "/aseet/kaytetyt-aseet/kivaarit/49/"
        ),
        AsetaloScraper(
            "/aseet/kaytetyt-aseet/haulikot/47/"
        ),
        AsetaloScraper(
            "/aseet/kaytetyt-aseet/pistoolit/7850/"
        ),
        AsetaloScraper(
            "/aseet/kaytetyt-aseet/pienoispistoolit/7866/"
        ),
        AsetaloScraper(
            "/aseet/kaytetyt-aseet/pienoiskivaarit/50/"
        )
    )

    Database("data/matsuri.db").use { database ->

        val repository =
            SqliteProductRepository(database.connection)

        val transactionRunner =
            JdbcTransactionRunner(database.connection)

        val tracker =
            ProductTracker(repository, transactionRunner)

        val service =
            MatsuriService(
                fetchers = fetchers,
                tracker = tracker
            )

        val results = service.run()

        val report =
            ProductReportService().create(results)

        ConsoleReportRenderer().render(report)
    }
}