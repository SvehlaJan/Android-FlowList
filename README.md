# FlowList / Gratitude Journal

My playground project to learn new tools from AndroidX, Jetpack and Kotlin.


Add `webClientId` to your local.properties. Follow https://firebase.google.com/docs/auth/android/google-signin for instructions.
Add 'giphyApiKey' to your local.properties. Follow https://developers.giphy.com/docs/api/quick-start/android for instructions.


The application is using Firebase Firestore ONLY for remote data storage.
It has great caching capabilities but for demonstration purposes it is not used.
Caching is done in the local database, with use of Room.


What is left to do:
- Refresh data when user logs out
- Delete entry from history
- Lazy loading for history