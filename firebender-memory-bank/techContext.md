- Technologies used
- Development setup
- Technical constraints
- Dependencies

## Technologies & Libraries
- Primary language: Kotlin & Kotlin Multiplatform
- UI: Compose Multiplatform https://www.jetbrains.com/compose-multiplatform/
- Networking: Ktor https://ktor.io/
- Image Catching: landscapist https://skydoves.github.io/landscapist/
- Local storage: sqldelight https://sqldelight.github.io/sqldelight/2.0.2/
- State Management: Lifecycle ViewModel 2.9.0-alpha13 https://developer.android.com/reference/kotlin/androidx/lifecycle/package-summary
- dependency injection: koin https://insert-koin.io/
- navigation: voyager https://voyager.adriel.cafe/
- rich editor: Compose Rich Editor https://mohamedrejeb.github.io/compose-rich-editor/
- local preferences: DataStore https://developer.android.com/kotlin/multiplatform/datastore

## Doc Comments
- Document code elements such as classes, methods, functions, and variables
- Include concise summaries and avoid redundancy with surrounding context
- Use examples and explanations for parameters, return values, and exceptions where helpful

## Testing
- create Unit tests for business logic classes
    - Tests should not share state.
