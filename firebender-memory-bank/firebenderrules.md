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

## Testing Guidelines
- Unit tests should not share state
- Mock dependencies for isolated testing
- Test business logic thoroughly
- Include UI tests for critical user flows
- Platform-specific tests should be in their respective source sets (androidTest, etc.)
- Common tests should use platform-agnostic approaches when possible

## Cross-Platform Guidelines
- Implement platform-specific dependencies via interface abstractions
- Register platform-specific implementations in the platform's dependency injection module
- Use expect/actual for platform-specific code

## Room Multiplatform Guidelines
- Use Room version 2.7.0-rc03 or newer for Kotlin Multiplatform support
- Android platform should use traditional Room initialization with context
- JVM platform should use BundledSQLiteDriver
- Ensure all DAOs use suspend functions or Flow returns for KMP compatibility