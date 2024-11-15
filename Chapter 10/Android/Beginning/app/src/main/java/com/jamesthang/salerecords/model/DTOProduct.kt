package com.jamesthang.salerecords.model

import java.util.UUID

data class DTOProduct(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val imageRes: Int = 0,
    val imageUrl: String = ""
)

