package com.jamesthang.salerecords.records

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
import com.jamesthang.salerecords.model.DTOSaleRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RecordItemView(
    index: Int,
    model: DTOSaleRecord,
    onRemoveClick: (DTOSaleRecord) -> Unit,
    onItemClick: (DTOSaleRecord) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .padding(horizontal = 10.dp)
            .clickable {
                onItemClick.invoke(model)
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
            text = "${model.sale} USD",
            modifier = Modifier.weight(0.4f),
            textAlign = TextAlign.Center
        )

        Text(
            text = formatTimestamp((model.date * 1000).toLong()),
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


fun formatTimestamp(timestamp: Long): String {
    val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val date = Date(timestamp)
    return simpleDateFormat.format(date)
}