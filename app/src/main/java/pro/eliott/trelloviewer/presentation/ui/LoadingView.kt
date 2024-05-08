package pro.eliott.trelloviewer.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import pro.eliott.trelloviewer.presentation.entities.Board

@Composable
fun Loading(resourceName : String? = null) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Loading " + (resourceName ?: ""));
        CircularProgressIndicator(
            indicatorColor = MaterialTheme.colors.primary,
            trackColor = MaterialTheme.colors.onBackground.copy(alpha = 0.1f),
            strokeWidth = 4.dp
        )
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewLoading() {
    Loading()
}