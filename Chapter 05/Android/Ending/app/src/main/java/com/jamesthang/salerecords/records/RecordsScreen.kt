package com.jamesthang.salerecords.records

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun RecordsScreen(viewModel: RecordsViewModel = hiltViewModel()) {
    val saleRecords = viewModel.saleRecords.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        if (saleRecords.value.isNotEmpty()) {
            itemsIndexed(saleRecords.value) { index, dtoSaleRecord ->
                RecordItemView(index = index + 1, model = dtoSaleRecord) {
                    viewModel.onRemoveClick(it)
                }
            }
        }
    }
}