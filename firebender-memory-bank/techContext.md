## Technologies used
## Development setup
## Technical constraints
## Dependencies

## Technologies & Libraries
- Primary language: Kotlin & Kotlin Multiplatform
- UI: Compose Multiplatform https://www.jetbrains.com/compose-multiplatform/
- Networking: Ktor https://ktor.io/
- Image Catching: coil https://coil-kt.github.io/coil/getting_started/
- Local storage: room 2.7.0-rc03 https://developer.android.com/kotlin/multiplatform/room
- State Management: Lifecycle ViewModel 2.9.0-alpha13 https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-lifecycle.html
- dependency injection: koin https://insert-koin.io/
- navigation: Compose Navigation component 2.9.0-alpha15 https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-navigation-routing.html
- rich editor: Compose Rich Editor https://mohamedrejeb.github.io/compose-rich-editor/
- local preferences: Multiplatform Settings https://github.com/russhwolf/multiplatform-settings
- Icons: composeIcons - Feather Icons https://github.com/DevSrSouza/compose-icons/blob/master/feather/DOCUMENTATION.md
- Logging: kermit https://kermit.touchlab.co/docs/

## Doc Comments
- Document code elements such as classes, methods, functions, and variables
- Include concise summaries and avoid redundancy with surrounding context
- Use examples and explanations for parameters, return values, and exceptions where helpful

## Testing
- create Unit tests for business logic classes
    - Tests should not share state.
- Use `androidTest` source set for instrumented tests of Room database
- Use `androidx.room:room-testing` library for testing migrations
- Create an in-memory database for testing:

## Room KMP Implementation Notes
- Room for Kotlin Multiplatform is relatively new (introduced in version 2.7.0-alpha01 on May 1, 2024)
- Current version (2.7.0-rc03) supports Android, iOS, JVM (Desktop), native Mac and native Linux platforms
- Key implementation differences from standard Android Room:
  - Android platform uses traditional database builder with context
  - Other platforms use SQLite drivers with new Room KMP APIs
  - KMP version requires DAO functions to be either suspend functions or return Flow
- Platform-specific initialization:
  - Android: Use `Room.databaseBuilder(context, AppDatabase::class.java, "db-name").build()`
  - iOS/Other: Use `Room.databaseBuilder("db-name").setDriver(driver).build()`
- Resources:
  - Official documentation: https://developer.android.com/kotlin/multiplatform/room
  - Migration guide: https://developer.android.com/training/data-storage/room/room-kmp-migration
