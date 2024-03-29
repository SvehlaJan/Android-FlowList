# FlowList / Gratitude Journal

My playground project to learn new tools from AndroidX, Jetpack and Kotlin.

## What does it do?

Gratitude journal is a simple app that allows you to write down your gratitude for the day. App
consists of 3 screens:

- *History screen* - list of user’s gratitude entries from the past. After clicking on an item, a Detail screen is opened.
- *Detail screen* - allows users to edit the gratitude entry. They can edit the notes and add a GIF image representing the day.
- *Settings screen* - allows users to sync the journal over multiple devices by signing in to Google account. Also lists used software libraries.


The application is using MVVM + Clean Architecture.

UI is done with Jetpack Compose. Only AndroidView component is Giphy picker.

Dependency injection is handled with Hilt.

Asynchronous tasks and handling of data is done with Kotlin Flows + Coroutines.

REST API calls are done with Ktor and Kotlinx Serialization.

Caching is done in the local database, with use of Room.

If the user isn't signed in, only the local data is available.

If the user signs in, the database will be filled with entries from API.

For authentication, Firebase Auth is used.

For selecting a GIF image, the app uses the Giphy.


# How to run

- Add `webClientId` to your local.properties and `google-services.json` to /src folder.
  Follow https://firebase.google.com/docs/auth/android/google-signin for instructions.
- Add `giphyApiKey` to your local.properties.
  Follow https://developers.giphy.com/docs/api/quick-start/android for instructions.

## TODOs:

- Add tests! :-)
- UX improvements
- Save entry when user clicks on item in bottom bar (utilise Compose side-effects?)

## Future development:

- Functionality to migrate existing local data to remote after sign-in
- CI/CD
- Delete entry from history
- Lazy loading for history
