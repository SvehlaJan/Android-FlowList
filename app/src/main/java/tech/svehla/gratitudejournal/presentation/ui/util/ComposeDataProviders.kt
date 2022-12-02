package tech.svehla.gratitudejournal.presentation.ui.util

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import tech.svehla.gratitudejournal.presentation.model.JournalEntryVO

class JournalEntryVODataProvider : PreviewParameterProvider<JournalEntryVO> {
    override val values = sequenceOf(JournalEntryVO.mock(1))
}

class JournalEntryVOListDataProvider : PreviewParameterProvider<List<JournalEntryVO>> {
    override val values = sequenceOf(listOf(JournalEntryVO.mock(1)))
}

private fun JournalEntryVO.Companion.mock(i: Int): JournalEntryVO {
    val dayStr = if (i < 10) "0$i" else i.toString()
    val date = "2022-12-$dayStr"
    return JournalEntryVO(
        date = date,
        firstNote = "First note $i",
        secondNote = "Second note $i",
        thirdNote = "Third note $i",
        gifUrl = "https://media.giphy.com/media/3o7TKsQ8U1iZaMe9bO/giphy.gif",
        imageUrl = "https://images.unsplash.com/photo-1619824780929-8c1b0b2b2f1d?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80",
    )
}