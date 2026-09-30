package io.github.stscoundrel.matsuri.domain

data class Product(
    val category: String,
    val id: String,
    val name: String,
    val hasImage: Boolean
)