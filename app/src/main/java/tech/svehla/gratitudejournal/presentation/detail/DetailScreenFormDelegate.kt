package tech.svehla.gratitudejournal.presentation.detail

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import tech.svehla.gratitudejournal.domain.model.JournalEntry
import java.time.LocalDate
import javax.inject.Inject


interface DetailScreenFormDelegate {
    val date: MutableState<String>
    val firstNote: MutableState<String>
    val secondNote: MutableState<String>
    val thirdNote: MutableState<String>
    val gifUrl: MutableState<String?>
    val dayScore: MutableState<Int?>
    val favoriteEntry: MutableState<Int?>
    val imageUrl: MutableState<String?>
    fun initValues(entry: JournalEntry)
    fun getNewEntry(): JournalEntry
    fun hasChanges(): Boolean
}

class DetailScreenFormDelegateImpl @Inject constructor() : DetailScreenFormDelegate {
    override var date = mutableStateOf("")
    override var firstNote = mutableStateOf("")
    override var secondNote = mutableStateOf("")
    override var thirdNote = mutableStateOf("")
    override var gifUrl = mutableStateOf<String?>(null)
    override var dayScore = mutableStateOf<Int?>(null) // not used yet
    override var favoriteEntry = mutableStateOf<Int?>(null) // not used yet
    override var imageUrl = mutableStateOf<String?>(null) // not used yet
    private var originalEntry: JournalEntry? = null

    override fun initValues(entry: JournalEntry) {
        originalEntry = entry
        date.value = entry.date
        firstNote.value = entry.firstNote
        secondNote.value = entry.secondNote
        thirdNote.value = entry.thirdNote
        imageUrl.value = entry.imageUrl
        gifUrl.value = entry.gifUrl
        dayScore.value = entry.dayScore
        favoriteEntry.value = entry.favoriteEntry
    }

    override fun getNewEntry() = JournalEntry(
        date = date.value,
        firstNote = firstNote.value,
        secondNote = secondNote.value,
        thirdNote = thirdNote.value,
        imageUrl = imageUrl.value,
        gifUrl = gifUrl.value,
        dayScore = dayScore.value,
        favoriteEntry = favoriteEntry.value,
        lastModified = LocalDate.now().toString()
    )

    override fun hasChanges(): Boolean {
        val isEqual = getNewEntry().isContentEqual(originalEntry ?: return false)
        return !isEqual
    }
}