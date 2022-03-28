# FlowList / Gratitude Journal

My playground project to learn new tools from AndroidX, Jetpack and Kotlin.

## What does it do?

Gratitude journal is a simple app that allows you to write down your gratitude for the day. App
consists of 3 screens:

- History screen - list of all your gratitude entries. After clicking on an item, a Detail screen is
  opened with specific entry.
- Detail screen - allows you to edit the entry - edit notes and add a GIF image.
- Settings screen - allows to sign in to your Google account and sign out.


The application is using Firebase Firestore ONLY for remote data storage - instead of REST API.
Firestore has great caching capabilities but for demonstration purposes it is not used.

Caching is done in the local database, with use of Room.

If the user isn't signed in, only the local data is available.

If the user signs in, the database will be filled with entries from Firebase Firestore.

For authentication, Firebase Auth is used.

For selecting a GIF image, the app uses the Giphy.

# How to run

- Add `webClientId` to your local.properties and `google-services.json` to /src folder.
  Follow https://firebase.google.com/docs/auth/android/google-signin for instructions.
- Add `giphyApiKey` to your local.properties.
  Follow https://developers.giphy.com/docs/api/quick-start/android for instructions.

## Immediate TODOs:

- Add more tests!
- UX improvements
- Save entry when user clicks on item in bottom bar

## Future development:

- Functionality to migrate existing local data to Firestore after sign-in
- CI/CD
- Replace Firebase Firestore with REST API
- Delete entry from history
- Lazy loading for history