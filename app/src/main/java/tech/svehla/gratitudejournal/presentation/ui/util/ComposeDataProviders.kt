package tech.svehla.gratitudejournal.presentation.ui.util

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import tech.svehla.gratitudejournal.domain.model.UserEntity
import tech.svehla.gratitudejournal.presentation.model.JournalEntryVO
import tech.svehla.gratitudejournal.presentation.settings.SettingsScreenState

class JournalEntryVODataProvider : PreviewParameterProvider<JournalEntryVO> {
    override val values = sequenceOf(JournalEntryVO.mock(1))
}

class JournalEntryVOListDataProvider : PreviewParameterProvider<List<JournalEntryVO>> {
    override val values = sequenceOf(listOf(JournalEntryVO.mock(1)))
}

class SettingsScreenStateDataProvider : PreviewParameterProvider<SettingsScreenState> {
    override val values = sequenceOf(
        SettingsScreenState(currentUser = UserEntity.mock(1)),
        SettingsScreenState(isLoading = true),
        SettingsScreenState(signInErrorMessage = "Sign in error message"),
    )
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

private fun SettingsScreenState.Companion.mock(i: Int): SettingsScreenState {
    return SettingsScreenState(
        currentUser = UserEntity.mock(i),
        isLoading = i % 2 == 0,
        signInErrorMessage = if (i % 3 == 0) "Error message $i" else null,
    )
}

private fun UserEntity.Companion.mock(i: Int): UserEntity {
    return UserEntity(
        displayName = "User $i",
        email = "user$i@email.com",
        photoUrl = "https://images.unsplash.com/photo-1619824780929-8c1b0b2b2f1d?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80",
        id = "user$i"
    )
}