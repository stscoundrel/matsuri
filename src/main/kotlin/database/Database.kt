package io.github.stscoundrel.matsuri.database

import java.sql.Connection
import java.sql.DriverManager

class Database(
    path: String
) : AutoCloseable {

    val connection: Connection =
        DriverManager.getConnection("jdbc:sqlite:$path")

    init {
        Schema.initialize(connection)
    }

    override fun close() {
        connection.close()
    }
}