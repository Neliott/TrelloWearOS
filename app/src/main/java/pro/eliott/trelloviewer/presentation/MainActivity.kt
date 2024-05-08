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
import pro.eliott.trelloviewer.presentation.ui.BoardDetail
import pro.eliott.trelloviewer.presentation.ui.BoardList
import pro.eliott.trelloviewer.presentation.ui.Loading
import pro.eliott.trelloviewer.presentation.ui.TaskCardDetail
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
            WearApp(viewModel)
        }
    }

    @Composable
    fun WearApp(viewModel: MainViewModel) {
        val uiState by viewModel.uiState.collectAsState()

        BoardNavigation(viewModel, uiState.boards)
    }

    @Composable
    fun BoardNavigation(viewModel: MainViewModel, boards: List<Board>?) {
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
                        Loading()
                    }
                }
                composable("board_detail/{id}") {
                    val id = it.arguments?.getString("id")!!
                    viewModel.fetchBoard(id)
                    BoardDetail(viewModel, id = id, onCardClicked = { card_id ->
                        navController.navigate("board_detail/$id/card/$card_id")
                    })
                }
                composable("board_detail/{id}/card/{card_id}") {
                    val boardId = it.arguments?.getString("id")!!
                    viewModel.fetchBoard(boardId)
                    val id = it.arguments?.getString("card_id")!!
                    TaskCardDetail(viewModel, boardId, id)
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun PreviewWearApp() {
        BoardNavigation(viewModel, boards = listOf(
            Board("1", "Workout", "Workout board", true),
            Board("2", "Workout", "Workout board", false),
        ))
    }
}