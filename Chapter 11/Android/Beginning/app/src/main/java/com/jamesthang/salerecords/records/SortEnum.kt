package com.jamesthang.salerecords.records

sealed class SortEnum(val title: String) {
    data object TimeAscending : SortEnum("Time Ascending")
    data object TimeDescending : SortEnum("Time Descending")

    data object SaleAscending : SortEnum("Sale Ascending")

    data object SaleDescending : SortEnum("Sale Descending")

    fun getFilterOption() = when (this) {
        TimeAscending -> Pair("date", false)
        TimeDescending -> Pair("date", true)
        SaleAscending -> Pair("sale", false)
        SaleDescending -> Pair("sale", true)
    }
}