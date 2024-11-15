package com.jamesthang.salerecords.model

data class DTOOrderRecord(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Int
) {
    fun total(): Double {
        return price * quantity
    }
}
