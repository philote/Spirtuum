# Firebender Rules for Spirituum Project

## Development Approach
- Follow TDD (Test-Driven Development) for all features
- Write tests first, then implement the necessary code
- Keep tests focused and isolated
- Always run a Gradle sync after making updates to Gradle files and fix errors or notify the user before moving on

## Project Scope
- No authentication or user management features in current scope
- Focus solely on recipe management functionality

## Architecture Guidelines
- Maintain strict separation between UI, Domain, and Data layers
- Repository pattern for data access
- Use cases for business logic
- ViewModels for UI state management

## Code Style
- Follow Kotlin coding conventions
- Include meaningful docstrings for public APIs
- Keep functions small and focused
- Never use println() for logging; always use the Kermit logging library:
  - Create loggers with appropriate tags: `private val logger = Logger.withTag("ComponentName")`
  - Use appropriate log levels:
    - `logger.v { "Verbose message" }` for highly detailed tracing
    - `logger.d { "Debug message" }` for debugging information
    - `logger.i { "Info message" }` for important runtime events
    - `logger.w { "Warning message" }` or `logger.w(exception) { "Warning with exception" }` for warnings
    - `logger.e { "Error message" }` or `logger.e(exception) { "Error with exception" }` for errors
  - Use lambda syntax `{ }` for log messages to avoid string concatenation when logging is disabled
  - Include useful contextual information in log messages

## Testing Guidelines
- Unit tests should not share state
- Mock dependencies for isolated testing
- Test business logic thoroughly
- Include UI tests for critical user flows
- Platform-specific tests should be in their respective source sets (androidTest, etc.)
- Common tests should use platform-agnostic approaches when possible
- Use proper coroutine testing techniques:
  - TestScope with UnconfinedTestDispatcher for controlling coroutine execution
  - Use advanceUntilIdle() to ensure all coroutines complete before assertions
  - Set up Main dispatcher in tests with setMain and resetMain
  - Follow camelCase naming for test functions (not backtick-enclosed names)

## Running Tests
- Run ViewModel tests with: `./gradlew :composeApp:jvmTest --tests "com.josephhopson.sprituum.ui.package.ClassNameTest"`
- Run multiple tests in a package: `./gradlew :composeApp:jvmTest --tests "com.josephhopson.sprituum.ui.package.*"`
- Run all tests: `./gradlew :composeApp:allTests` (includes Android and iOS tests which may fail differently)
- UI Component tests using `runComposeUiTest` currently fail with NullPointerException and need additional configuration
- Focus testing efforts on ViewModelTests and ScreenTests as they provide the best coverage for business logic
- The `--tests` flag works only with the jvmTest task, not with Android or iOS test tasks

## Cross-Platform Guidelines
- Implement platform-specific dependencies via interface abstractions
- Register platform-specific implementations in the platform's dependency injection module
- Use expect/actual for platform-specific code
- Platform-specific code should be isolated in dedicated files
- Navigation can be implemented without heavyweight libraries for simple use cases
- When implementing platform-specific resources:
  - Place common resources in commonMain/resources directory
  - Define expect functions in commonMain
  - Implement actual functions in each platform's source set
  - Follow the same package structure across all platforms
  - Use appropriate dispatchers for resource loading (IO for JVM/Android, Default for iOS)

## Room Multiplatform Guidelines
- Use Room version 2.7.0-rc03 or newer for Kotlin Multiplatform support
- Android platform should use traditional Room initialization with context
- JVM platform should use BundledSQLiteDriver
- Ensure all DAOs use suspend functions or Flow returns for KMP compatibility

## Compose UI Guidelines
- Avoid nesting scrollable components (e.g., LazyColumn inside Column with verticalScroll)
- When using a scrollable container:
  - Either use a top-level LazyColumn/LazyVerticalGrid for dynamic lists
  - Or use Column with verticalScroll containing static components
  - Never combine both in parent-child relationship
- Use proper height constraints for scrollable content
- Pay attention to the following error: "Vertically scrollable component was measured with an infinity maximum height constraints"
  - This usually indicates a nested scrolling issue
- For complex forms with lists:
  - Place everything in a single scrollable container
  - Use regular Column with forEachIndexed instead of LazyColumn for sections containing lists
- Always test scroll behavior on different screen sizes

## Material Icons Usage
- For basic icons, import from `androidx.compose.material.icons.Icons`
- Use `Icons.Filled.[IconName]` for filled icons (with proper capitalization)
- For directional icons that should respect RTL languages, use `Icons.AutoMirrored.Filled.[IconName]` namespace
  - Example: `Icons.AutoMirrored.Filled.Sort`, `Icons.AutoMirrored.Filled.List`
- For extended icon set, ensure `compose.materialIconsExtended` dependency is added to the project
- Icon namespaces are case-sensitive: use `Filled` not `filled`
- Common icons available include:
  - Basic icons: Add, Close, Check, Search, Dashboard
  - AutoMirrored icons: List, Sort
  - Extended icons: FilterList, ViewModule

## Material 3 Component Usage
- When using Material 3 experimental components like DockedSearchBar, be aware of API changes
- The DockedSearchBar API has changed to prefer a new `inputField` parameter approach over direct parameters
- When encountering compatibility issues with newer APIs, use `@Suppress("DEPRECATION")` and fall back to the older API
- Monitor Material 3 updates as experimental APIs may change between releases
- Material 3 components often have `*Defaults` companion objects that provide preset values and helper functions

## Recipe Guidelines
- When creating new recipes, follow the established JSON format
- Place recipe JSON files in commonMain/resources/recipes directory
- Required fields for recipes:
  - name (String)
  - createdAt (Timestamp)
  - updatedAt (Timestamp)
- Include these fields for better user experience:
  - about (String): Background info about the cocktail
  - instructions (List of String): Step-by-step preparation
  - ingredients (List of Ingredient objects): Components with amounts and units
  - tags (List of String): For categorization and filtering
- For ancestral cocktails, include the "ancestral" tag
- High-quality recipes should include historical context and expert tips in the "about" and "notes" fields
