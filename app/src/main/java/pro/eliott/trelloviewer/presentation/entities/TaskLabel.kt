package pro.eliott.trelloviewer.presentation.entities

import androidx.compose.ui.graphics.Color

data class TaskLabel(
    val id: String,
    val name: String,
    val color: String,
){
    fun color(): Color {
        return when (color) {
            "green" -> Color.Green
            "yellow" -> Color.Yellow
            "red" -> Color.Red
            "blue" -> Color.Blue
            "black" -> Color.Black
            else -> Color.Gray
        }
    }
    fun textColor(): Color {
        return when (color) {
            "green" -> Color.White
            "yellow" -> Color.Black
            "red" -> Color.White
            "blue" -> Color.White
            "black" -> Color.White
            else -> Color.Black
        }
    }
}