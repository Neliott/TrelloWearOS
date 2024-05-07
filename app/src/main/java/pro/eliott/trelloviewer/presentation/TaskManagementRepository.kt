package pro.eliott.trelloviewer.presentation

import pro.eliott.trelloviewer.presentation.entities.Board
import pro.eliott.trelloviewer.presentation.entities.TaskCard

interface TaskManagementRepository {
    fun boards(): List<Board>;
    fun boardCards(id: String): List<TaskCard>;
}