package com.jamesthang.salerecords.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.jamesthang.salerecords.R
import com.jamesthang.salerecords.model.DTOProduct

@Composable
fun MenuItemView(model: DTOProduct, onProductClick: (DTOProduct) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable {
                onProductClick.invoke(model)
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Image(
            painter = // Placeholder resource
            rememberAsyncImagePainter(ImageRequest.Builder // Error resource
                (LocalContext.current).data(data = model.imageUrl).apply<ImageRequest.Builder>(block = fun ImageRequest.Builder.() {
                placeholder(R.drawable.ic_black_coffee) // Placeholder resource
                error(R.drawable.ic_launcher_foreground) // Error resource
            }).build()
            ),
            contentDescription = null,
            modifier = Modifier
                .height(48.dp)
                .weight(0.2f),
            alignment = Alignment.CenterStart
        )
        Text(text = model.name, modifier = Modifier.weight(0.4f))
        Text(text = "${model.price} USD", modifier = Modifier.weight(0.4f))

    }
}