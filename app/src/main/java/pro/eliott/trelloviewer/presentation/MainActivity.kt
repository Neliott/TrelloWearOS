/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package pro.eliott.trelloviewer.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.format.DateUtils
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipColors
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TitleCard
import androidx.wear.compose.material.rememberScalingLazyListState
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.remote.interactions.RemoteActivityHelper
import pro.eliott.trelloviewer.presentation.entities.Board
import pro.eliott.trelloviewer.presentation.entities.TaskCard
import pro.eliott.trelloviewer.presentation.entities.TaskLabel
import pro.eliott.trelloviewer.presentation.theme.TrelloViewerTheme
import java.util.Date
import kotlin.math.ceil


class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels { MainViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        viewModel.fetchBoards()

        setContent {
            WearApp()
        }
    }

    @Composable
    fun WearApp() {
        val uiState by viewModel.uiState.collectAsState()

        BoardNavigation(uiState.boards)
    }

    @Composable
    fun BoardNavigation(boards: List<Board>?) {
        TrelloViewerTheme {
            val contentModifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
            val listState = rememberScalingLazyListState()

            val navController = rememberSwipeDismissableNavController()
            SwipeDismissableNavHost(
                navController = navController,
                startDestination = "board_list"
            ) {
                composable("board_list") {
                    if (boards != null) {
                        BoardList(boards, onBoardClicked = { id ->
                            navController.navigate("board_detail/$id")
                        })
                    } else {
                        Text("Loading")
                    }
                }
                composable("board_detail/{id}") {
                    val id = it.arguments?.getString("id")!!
                    viewModel.fetchBoard(id)
                    BoardDetail(id = id, onCardClicked = { card_id ->
                        navController.navigate("board_detail/$id/card/$card_id")
                    })
                }
                composable("board_detail/{id}/card/{card_id}") {
                    val boardId = it.arguments?.getString("id")!!
                    viewModel.fetchBoard(boardId)
                    val id = it.arguments?.getString("card_id")!!
                    TaskCardDetail(boardId, id)
                }
            }
        }
    }

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
    fun BoardDetail(id: String, onCardClicked: (String) -> Unit) {
        val uiState by viewModel.uiState.collectAsState()
        val board = uiState.boards?.find { it.id == id }!!
        if (uiState.cachedBoards.containsKey(id)) {
            val cards = uiState.cachedBoards[id]!!
            BoardCards(board, cards, onCardClicked)
        } else {
            Text("Loading")
        }
    }

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
    fun TaskCardDetail(boardId: String, id: String) {
        val uiState by viewModel.uiState.collectAsState()
        val card = uiState.cachedBoards[boardId]?.find { it.id == id }
        if (card != null) {
            TaskCardView(card)
        } else {
            Text("Loading")
        }
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
            startActivity(it)
        }
    }


    @Preview(showBackground = true)
    @Composable
    fun PreviewWearApp() {
        /*BoardNavigation(boards = listOf(
            Board("1", "Workout", "Workout board", true),
            Board("2", "Workout", "Workout board", false),
        ))*/
        /*BoardCards(board =  Board("1", "Workout", "Workout board"), cards =
            listOf(
                TaskCard("1", "Task 1", "Task 1 description", due = Date(2024, 5, 3, 16, 0)),
                TaskCard("2", "Task 2", "Task 2 description"),
                TaskCard("3", "Task 3", "Task 3 description"),
        ))*/
        TaskCardView(TaskCard("1", "Task 1", "Task 1 description", Date(), labels =
            listOf(TaskLabel("1", "Label 1", "red"), TaskLabel("2", "Label 2", "blue"), TaskLabel("2", "Label 3", "yellow"))
            , due = Date(2024, 5, 3, 16, 0)))
    }
}