package pro.eliott.trelloviewer.presentation.ui

import android.text.format.DateUtils
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TitleCard
import pro.eliott.trelloviewer.presentation.MainViewModel
import pro.eliott.trelloviewer.presentation.entities.Board
import pro.eliott.trelloviewer.presentation.entities.TaskCard
import java.util.Date

@Composable
fun BoardCards(board: Board, cards: List<TaskCard>, onCardClicked: (String) -> Unit = {}) {
    var openCards = cards.filter { !it.closed }
    val nextDueCards = openCards.filter { it.due != null }.sortedBy { it.due }
    val noDueOpenCards = openCards.filter { it.due == null }
    ScalingLazyColumn {
        item {
            Text(text = "Cards", style = MaterialTheme.typography.title1)
        }
        item {
            Text(text = "Next due cards", style = MaterialTheme.typography.caption1)
        }
        items(nextDueCards.size) { index ->
            val card = nextDueCards[index]
            BoardTaskCard(card, onCardClicked)
        }
        item {
            Text(text = "No due cards", style = MaterialTheme.typography.caption1)
        }
        items(noDueOpenCards.size) { index ->
            val card = noDueOpenCards[index]
            BoardTaskCard(card, onCardClicked)
        }
    }
}


@Composable
fun BoardTaskCard(card: TaskCard, onCardClicked: (String) -> Unit) {
    val current = System.currentTimeMillis()
    val due = card.due?.time ?: current
    TitleCard(
        onClick = { onCardClicked(card.id) },
        title = { Text(card.name) },
        contentColor = MaterialTheme.colors.onSurface,
        titleColor = MaterialTheme.colors.onSurface
    ) {
        Column {
            if (card.due != null) {
                Text(
                    DateUtils.getRelativeTimeSpanString(due, current, DateUtils.DAY_IN_MILLIS)
                        .toString(), style = MaterialTheme.typography.caption2
                )
            }
        }
    }
}

@Composable
fun TaskCardDetail(viewModel: MainViewModel, boardId: String, id: String) {
    val uiState by viewModel.uiState.collectAsState()
    val card = uiState.cachedBoards[boardId]?.find { it.id == id }
    if (card != null) {
        TaskCardView(card)
    } else {
        Loading()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBoard() {
    BoardCards(board =  Board("1", "Workout", "Workout board"), cards =
    listOf(
        TaskCard("1", "Task 1", "Task 1 description",  Date(2024, 5, 3, 16, 0), due = Date(2024, 5, 3, 16, 0)),
        TaskCard("2", "Task 2", "Task 2 description",  Date(2024, 5, 3, 16, 0)),
        TaskCard("3", "Task 3", "Task 3 description",  Date(2024, 5, 3, 16, 0)),
    ))
}