package pro.eliott.trelloviewer.presentation.trello

import com.google.gson.Gson
import pro.eliott.trelloviewer.presentation.TRELLO_API_ENDPOINT
import pro.eliott.trelloviewer.presentation.TRELLO_API_KEY
import pro.eliott.trelloviewer.presentation.TRELLO_API_TOKEN
import pro.eliott.trelloviewer.presentation.TaskManagementRepository
import pro.eliott.trelloviewer.presentation.entities.Board
import pro.eliott.trelloviewer.presentation.entities.TaskCard
import java.net.URL

class TrelloApi : TaskManagementRepository {
    val QUERY_STRING = "key=$TRELLO_API_KEY&token=$TRELLO_API_TOKEN"

    override fun boards(): List<Board> {
        val url = "$TRELLO_API_ENDPOINT/members/me/boards?$QUERY_STRING"
        val string = URL(url).readText()
        val gson: Gson = Gson()
        val boards: List<Board> = gson.fromJson(string, Array<Board>::class.java).toList()
        return boards
    }

    override fun boardCards(boardId: String): List<TaskCard> {
        val url = "$TRELLO_API_ENDPOINT/boards/${boardId}/cards?$QUERY_STRING"
        val string = URL(url).readText()
        val gson: Gson = Gson()
        val tasks: List<TaskCard> = gson.fromJson(string, Array<TaskCard>::class.java).toList()
        return tasks
    }
}