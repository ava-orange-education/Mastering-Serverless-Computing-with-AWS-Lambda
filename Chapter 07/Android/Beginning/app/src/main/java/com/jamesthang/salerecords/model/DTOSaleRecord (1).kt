package com.jamesthang.salerecords.model

import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize

@Parcelize
data class DTOSaleRecord(
    val date: Double = 0.0,
    val sale: Double = 0.0,
    val products: List<DTOSaleRecordProduct> = emptyList(),
    var id: String = ""
) : Parcelable

@Parcelize
data class DTOSaleRecordProduct(
    val productId: String = "",
    val productName: String = "",
    val quantity: Int = 0,
    val subtotal: Double = 0.0,
    val unitPrice: Double = 0.0
) : Parcelable

class DTOSaleRecordNavType : NavType<DTOSaleRecord>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): DTOSaleRecord? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): DTOSaleRecord {
        return Gson().fromJson(value, DTOSaleRecord::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: DTOSaleRecord) {
        bundle.putParcelable(key, value)
    }
}