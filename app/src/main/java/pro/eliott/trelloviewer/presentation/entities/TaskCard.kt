package pro.eliott.trelloviewer.presentation.entities

import android.text.format.DateUtils
import androidx.compose.ui.graphics.Color
import java.util.Date

data class TaskCard(
    val id: String,
    val name: String,
    val desc: String,
    val dateLastActivity: Date,
    val closed: Boolean = false,
    val dueComplete: Boolean = false,
    val labels: List<TaskLabel> = emptyList(),
    val due: Date? = null,
    val shortUrl : String = "",
    private val dueReminder: Number = 0,
    private val idList: String = ""
){
    fun dueColor(): Color {
        if (dueComplete) {
            return Color.Green
        }
        val current = System.currentTimeMillis()
        val dueTime = due?.time ?: return Color.Gray
        return if (dueTime < current) {
            Color.Red
        } else if (dueTime - current < dueReminder.toLong() * DateUtils.MINUTE_IN_MILLIS) {
            Color.Yellow
        } else {
            Color.Green
        }
    }
}