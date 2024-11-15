package com.jamesthang.salerecords.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DTOSaleRecord(
    val date: Double = 0.0,
    val sale: Double = 0.0,
    var id: String = ""
) : Parcelable