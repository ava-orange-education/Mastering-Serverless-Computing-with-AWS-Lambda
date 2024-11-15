package com.jamesthang.salerecords.menu

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MenuScreen(viewModel: MenuViewModel = hiltViewModel()) {

    val productList = viewModel.productList.collectAsState()
    val orderList = viewModel.orderRecords.collectAsState()

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (contentView, orderView) = createRefs()
        val context = LocalContext.current

        ElevatedButton(
            onClick = {
                viewModel.onOrderClick()
//                viewModel.uploadAllImagesToFirebaseStorage(context)
            },
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(orderView) {
                    bottom.linkTo(parent.bottom, 20.dp)
                    start.linkTo(parent.start, 20.dp)
                    end.linkTo(parent.end, 20.dp)
                    width = Dimension.fillToConstraints
                }) {
            Text(text = "ORDER")
//            Text(text = "UPLOAD ALL PRODUCTS")
        }

        LazyColumn(modifier = Modifier.constrainAs(contentView) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(orderView.top)
            width = Dimension.fillToConstraints
            height = Dimension.fillToConstraints
        }, contentPadding = PaddingValues(10.dp)) {
            item {
                Text(text = "MENU")
            }

            items(productList.value) {
                MenuItemView(model = it) { dtoProduct ->
                    viewModel.onProductClick(dtoProduct)
                }
            }

            if (orderList.value.isNotEmpty()) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                    ) {
                        Text(text = "SELECT: ")

                        Text(text = "${orderList.value.sumOf { it.total() }} USD")
                    }
                }

                itemsIndexed(orderList.value) { index, dtoOrderRecord ->
                    OrderItemView(index = index + 1, model = dtoOrderRecord) {
                        viewModel.onOrderRemoveClick(it)
                    }
                }
            }
        }
    }
}