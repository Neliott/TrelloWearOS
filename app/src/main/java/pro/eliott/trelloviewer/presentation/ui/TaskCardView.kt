package pro.eliott.trelloviewer.presentation.ui

import android.content.Intent
import android.net.Uri
import android.text.format.DateUtils
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.remote.interactions.RemoteActivityHelper
import pro.eliott.trelloviewer.presentation.entities.TaskCard
import pro.eliott.trelloviewer.presentation.entities.TaskLabel
import java.util.Date
import kotlin.math.ceil

@Preview(showBackground = true)
@Composable
fun PreviewTask() {
    TaskCardView(TaskCard("1", "Task 1", "Task 1 description", Date(), labels =
    listOf(TaskLabel("1", "Label 1", "red"), TaskLabel("2", "Label 2", "blue"), TaskLabel("2", "Label 3", "yellow"))
        , due = Date(2024, 5, 3, 16, 0)
    ))
}

@Composable
fun TaskCardView(card: TaskCard) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp, 32.dp)
    ) {
        //Cetner text
        Text(text = card.name, style = MaterialTheme.typography.title1)
        Text(
            text = (if (card.due != null) "Due: ${
                DateUtils.getRelativeTimeSpanString(
                    card.due!!.time,
                    System.currentTimeMillis(),
                    DateUtils.DAY_IN_MILLIS
                )
            }" else "No due date"),
            style = MaterialTheme.typography.caption1,
            color = card.dueColor()
        )
        Text(text = card.desc, style = MaterialTheme.typography.caption2)
        Column {
            for (index in 1..ceil( card.labels.size / 2f).toInt()) {
                Row {
                    val label1 = card.labels[index*2 - 2]
                    val label2 = card.labels.getOrNull(index*2 - 1)
                    CompactChip(
                        onClick = { },
                        colors = ChipDefaults.primaryChipColors(
                            backgroundColor = label1.color()
                        ),
                        border = ChipDefaults.chipBorder(),
                        label = {
                            Text(text = label1.name, style = MaterialTheme.typography.caption3, color = label1.textColor())
                        }
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    if (label2 != null) {
                        CompactChip(
                            onClick = { },
                            colors = ChipDefaults.primaryChipColors(
                                backgroundColor = label2.color()
                            ),
                            border = ChipDefaults.chipBorder(),
                            label = {
                                Text(text = label2.name, style = MaterialTheme.typography.caption3, color = label2.textColor())
                            }
                        )
                    }
                }
            }
        }
        /*Chip(
            onClick = { openLinkInPhone(card.shortUrl) },
            colors = ChipDefaults.primaryChipColors(),
            border = ChipDefaults.chipBorder(),
            shape = MaterialTheme.shapes.small,
            contentPadding = PaddingValues(16.dp)
        ) {
            Text(text = "Mark as done")
        }*/
        Spacer(modifier = Modifier.padding(4.dp))
        Chip(
            onClick = { openLinkInPhone(card.shortUrl) },
            colors = ChipDefaults.primaryChipColors(),
            border = ChipDefaults.chipBorder(),
            shape = MaterialTheme.shapes.small,
            contentPadding = PaddingValues(16.dp)
        ) {
            Text(text = "Open in phone")
        }
    }
}

fun openLinkInPhone(url: String) {
    val intent = Intent(Intent.ACTION_VIEW)
        .addCategory(Intent.CATEGORY_BROWSABLE)
        .setData(Uri.parse(url))

    RemoteActivityHelper.getTargetIntent(intent)?.let {
        //startActivity(it)
    }


}