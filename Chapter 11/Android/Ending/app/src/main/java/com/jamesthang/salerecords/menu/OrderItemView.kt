package com.jamesthang.salerecords.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jamesthang.salerecords.model.DTOOrderRecord

@Composable
fun OrderItemView(index: Int, model: DTOOrderRecord, onRemoveClick: (DTOOrderRecord) -> Unit) {
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
            text = "${model.name} x ${model.quantity}",
            modifier = Modifier.weight(0.4f),
            textAlign = TextAlign.Center
        )

        Text(
            text = "${model.total()} USD",
            modifier = Modifier.weight(0.25f),
            textAlign = TextAlign.Center
        )

        Icon(
            imageVector = Icons.Filled.Clear,
            contentDescription = null,
            modifier = Modifier
                .weight(0.25f)
                .align(Alignment.CenterVertically)
                .clickable {
                    onRemoveClick.invoke(model)
                }
        )
    }
}