# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Development Commands

- **Build the project**: `./gradlew build`
- **Clean build**: `./gradlew clean build`
- **Run unit tests**: `./gradlew test`
- **Run Android instrumented tests**: `./gradlew connectedAndroidTest`
- **Install debug APK**: `./gradlew installDebug`
- **Generate APK**: `./gradlew assembleDebug` or `./gradlew assembleRelease`
- **Lint code**: `./gradlew lint`

## Architecture Overview

This is an Android file archiving app built with Kotlin and Jetpack Compose using MVVM architecture:

### Core Architecture
- **MVVM Pattern**: Activities use ViewModels that interact with Repository layer
- **Repository Pattern**: `FileRepository` handles all file operations and data management
- **Flow-based**: Reactive UI using StateFlow/Flow for data streams
- **DataStore**: Preferences managed with AndroidX DataStore (not SharedPreferences)

### Key Components
- **FileRepository**: Central file management with Flow-based reactive updates
- **PreferencesManager**: Settings persistence using DataStore
- **ContentHandler**: Processes Android share intents and extracts file data
- **MainViewModel**: UI state management with proper coroutine handling

### File Storage Strategy
- Files stored in app's private directory: `{filesDir}/archived_files/`
- UUID-based naming: `{UUID}-{originalFileName}` to prevent conflicts
- Metadata extracted and cached in memory (FileItem objects)

### Dependency Management
- Uses `libs.versions.toml` for version catalogs
- Major dependencies: Compose BOM, Material 3, Navigation, DataStore, Markwon
- Testing: JUnit, Mockito, Coroutines Test

## Testing Strategy

Unit tests cover core business logic:
- FileUtils: formatting and file type detection
- PreferencesManager: settings persistence
- FileRepository: CRUD operations and sorting
- ContentHandler: intent processing

Run individual test classes:
- `./gradlew test --tests "com.giraone.archiver.FileUtilsTest"`
- `./gradlew test --tests "com.giraone.archiver.PreferencesManagerTest"`

## Development Notes

- Package structure follows feature-based organization under `com.giraone.archiver`
- Uses modern Android development practices: Compose, DataStore, Flow
- Internationalization support for English, German, French
- Material 3 theming with proper dark/light mode support
- No network dependencies - purely local file storage app