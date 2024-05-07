package pro.eliott.trelloviewer.presentation.entities

data class Board(
    val id: String,
    val name: String,
    val desc: String,
    val closed: Boolean = false,
    val starred: Boolean = false,
    val pinned: Boolean = false
)