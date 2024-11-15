package com.jamesthang.salerecords.model

import java.util.UUID

data class DTOProduct(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val price: Double,
    val imageRes: Int
)
