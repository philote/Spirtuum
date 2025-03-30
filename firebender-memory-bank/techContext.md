- Technologies used
- Development setup
- Technical constraints
- Dependencies

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
