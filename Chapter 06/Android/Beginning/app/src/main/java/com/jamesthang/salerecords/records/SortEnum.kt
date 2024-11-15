package com.jamesthang.salerecords.records

sealed class SortEnum(val title: String) {
    data object TimeAscending : SortEnum("Time Ascending")
    data object TimeDescending : SortEnum("Time Descending")

    data object SaleAscending : SortEnum("Sale Ascending")

    data object SaleDescending : SortEnum("Sale Descending")
}