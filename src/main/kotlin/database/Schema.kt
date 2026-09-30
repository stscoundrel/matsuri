package io.github.stscoundrel.matsuri.database

import java.sql.Connection

object Schema {

    fun initialize(connection: Connection) {
        connection.createStatement().use { statement ->
            statement.execute(
                """
                CREATE TABLE IF NOT EXISTS products (
                    category TEXT NOT NULL,
                    id TEXT NOT NULL,
                    name TEXT NOT NULL,
                    has_image INTEGER NOT NULL,
                    last_seen_at TEXT,
                    PRIMARY KEY (category, id)
                )
                """.trimIndent()
            )
        }
    }
}