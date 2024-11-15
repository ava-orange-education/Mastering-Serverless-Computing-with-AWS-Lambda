package com.jamesthang.salerecords.record_detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jamesthang.salerecords.model.DTOSaleRecord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordDetailScreen(navController: NavController, dtoSaleRecord: DTOSaleRecord) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Record Detail") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            itemsIndexed(dtoSaleRecord.products) { index, product ->
                RecordDetailItemView(index = index + 1, model = product)
            }
        }
    }
}