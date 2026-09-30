package io.github.stscoundrel.matsuri.database

import io.github.stscoundrel.matsuri.domain.Product
import io.github.stscoundrel.matsuri.domain.ProductRepository
import java.sql.Connection
import java.time.Instant

class SqliteProductRepository(
    private val connection: Connection
) : ProductRepository {

    override fun find(
        category: String,
        id: String
    ): Product? {
        connection.prepareStatement(
            """
            SELECT category, id, name, has_image
            FROM products
            WHERE category = ?
              AND id = ?
            """.trimIndent()
        ).use { statement ->

            statement.setString(1, category)
            statement.setString(2, id)

            statement.executeQuery().use { result ->
                if (!result.next()) {
                    return null
                }

                return Product(
                    category = result.getString("category"),
                    id = result.getString("id"),
                    name = result.getString("name"),
                    hasImage = result.getBoolean("has_image")
                )
            }
        }
    }

    override fun save(product: Product) {
        connection.prepareStatement(
            """
            INSERT INTO products (
                category,
                id,
                name,
                has_image,
                last_seen_at
            )
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT(category, id)
            DO UPDATE SET
                name = excluded.name,
                has_image = excluded.has_image,
                last_seen_at = excluded.last_seen_at
            """.trimIndent()
        ).use { statement ->

            statement.setString(1, product.category)
            statement.setString(2, product.id)
            statement.setString(3, product.name)
            statement.setBoolean(4, product.hasImage)
            statement.setString(5, Instant.now().toString())

            statement.executeUpdate()
        }
    }
}