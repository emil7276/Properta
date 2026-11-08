package id.properta.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import id.properta.app.R

@Composable
fun PropertaLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.properta_master),
        contentDescription = "Logo PROPERTA",
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}
