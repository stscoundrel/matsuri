package io.github.stscoundrel.matsuri.application

import io.github.stscoundrel.matsuri.database.Database
import io.github.stscoundrel.matsuri.database.JdbcTransactionRunner
import io.github.stscoundrel.matsuri.database.SqliteProductRepository
import io.github.stscoundrel.matsuri.domain.Product
import io.github.stscoundrel.matsuri.domain.ProductFetcher
import java.sql.SQLException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProductTrackerTransactionTest {
    @Test
    fun `successful category commits products and reports only image transitions`() {
        Database(":memory:").use { database ->
            val repository = SqliteProductRepository(database.connection)
            val transactionRunner = JdbcTransactionRunner(database.connection)
            val previous = Product("category", "existing", "Existing", false)
            repository.save(previous)
            val updated = previous.copy(hasImage = true)
            val newProduct = Product("category", "new", "New", true)

            val result = ProductTracker(repository, transactionRunner).run(fetcher(updated, newProduct))

            assertEquals(listOf(updated), result.newImages)
            assertEquals(updated, repository.find("category", "existing"))
            assertEquals(newProduct, repository.find("category", "new"))
            assertTrue(database.connection.autoCommit)
        }
    }

    @Test
    fun `failed category rolls back all its updates and can be retried`() {
        Database(":memory:").use { database ->
            val repository = SqliteProductRepository(database.connection)
            val transactionRunner = JdbcTransactionRunner(database.connection)
            val tracker = ProductTracker(repository, transactionRunner)
            val previous = Product("category", "existing", "Existing", false)
            repository.save(previous)
            val committed = Product("other", "committed", "Committed", true)
            tracker.run(fetcher(committed))
            val updated = previous.copy(hasImage = true)
            val inserted = Product("category", "inserted", "Inserted", true)
            val failing = Product("category", "fail", "Fail", true)

            database.connection.createStatement().use { statement ->
                statement.execute(
                    """
                    CREATE TRIGGER fail_product BEFORE INSERT ON products
                    WHEN NEW.id = 'fail'
                    BEGIN
                        SELECT RAISE(ABORT, 'Simulated save failure');
                    END
                    """.trimIndent()
                )
            }

            assertFailsWith<SQLException> {
                tracker.run(fetcher(updated, inserted, failing))
            }

            assertEquals(previous, repository.find("category", "existing"))
            assertNull(repository.find("category", "inserted"))
            assertNull(repository.find("category", "fail"))
            assertEquals(committed, repository.find("other", "committed"))
            assertTrue(database.connection.autoCommit)

            database.connection.createStatement().use { it.execute("DROP TRIGGER fail_product") }
            val retried = tracker.run(fetcher(updated, inserted, failing))

            assertEquals(listOf(updated), retried.newImages)
            assertEquals(updated, repository.find("category", "existing"))
            assertEquals(inserted, repository.find("category", "inserted"))
            assertEquals(failing, repository.find("category", "fail"))
        }
    }

    private fun fetcher(vararg products: Product): ProductFetcher = object : ProductFetcher {
        override val category = products.first().category

        override fun fetchProducts(): List<Product> = products.toList()
    }
}
