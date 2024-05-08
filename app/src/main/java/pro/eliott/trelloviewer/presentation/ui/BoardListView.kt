package pro.eliott.trelloviewer.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TitleCard
import pro.eliott.trelloviewer.presentation.MainViewModel
import pro.eliott.trelloviewer.presentation.entities.Board

@Composable
fun BoardList(boards: List<Board>, onBoardClicked: (String) -> Unit) {
    val starredBoards = boards.filter { it.starred }
    var openBoards = boards.filter { !it.closed }
    Column {
        ScalingLazyColumn {
            item {
                Text(text = "Boards", style = MaterialTheme.typography.title1)
            }
            item {
                Text(text = "Starred boards", style = MaterialTheme.typography.caption1)
            }
            items(starredBoards.size) { index ->
                val board = starredBoards[index]
                BoardCard(board, onBoardClicked)
            }
            item {
                Text(text = "Other open boards", style = MaterialTheme.typography.caption1)
            }
            items(openBoards.size) { index ->
                val board = openBoards[index]
                BoardCard(board, onBoardClicked)
            }
        }
    }
}

@Composable
fun BoardCard(board: Board, onBoardClicked: (String) -> Unit) {
    TitleCard(
        onClick = { onBoardClicked(board.id) },
        title = { Text(board.name) },
        contentColor = MaterialTheme.colors.onSurface,
        titleColor = MaterialTheme.colors.onSurface
    ) {
        if (board.desc.isNotEmpty()) {
            Text(
                text = board.desc.take(100),
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}

@Composable
fun BoardDetail(viewModel: MainViewModel, id: String, onCardClicked: (String) -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val board = uiState.boards?.find { it.id == id }!!
    if (uiState.cachedBoards.containsKey(id)) {
        val cards = uiState.cachedBoards[id]!!
        BoardCards(board, cards, onCardClicked)
    } else {
        Loading()
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewBoardList() {
    BoardList(boards = listOf(
        Board("1", "Workout", "Workout board", true),
        Board("2", "Workout", "Workout board", false),
    ),{})
}