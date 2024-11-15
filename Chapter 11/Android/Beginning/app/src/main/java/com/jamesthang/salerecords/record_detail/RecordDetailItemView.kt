package com.jamesthang.salerecords.record_detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jamesthang.salerecords.model.DTOSaleRecordProduct

@Composable
fun RecordDetailItemView(index: Int, model: DTOSaleRecordProduct) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable {

            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = index.toString(),
            modifier = Modifier.weight(0.1f),
            textAlign = TextAlign.Start
        )

        Text(
            text = model.productName,
            modifier = Modifier.weight(0.45f),
            textAlign = TextAlign.Center
        )

        Text(
            text = "${model.unitPrice} USD",
            modifier = Modifier.weight(0.45f),
            textAlign = TextAlign.Center
        )
    }
}