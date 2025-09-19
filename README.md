# Archiver

This is/was a PoC to test the capabilities of *Claude Code* for generating and maintaining an Android app.

- See [prompts.md](documentation/ai/prompts.md), what I initially used as the input, what *Claude Code* returned
  and also some typically prompt/result scenarios
- See [requirements](documentation/requirements), what further steps/prompts I made to let *Claude Code*
  improve the app by passing more business and technical requirements to *Claude Code*

# Archiver - from the perspective of *Claude Code*

A simple and efficient Android app that allows you to receive and store files shared from other applications on your device.

## Features

- **File Reception**: Receives shared content from other apps via `ACTION_SEND` and `ACTION_SEND_MULTIPLE` intents
- **Secure Storage**: Stores files in the app's private directory with UUID-based naming to prevent conflicts
- **File Management**: Browse, sort, rename, delete, and display your archived files
- **Multiple File Types**: Supports images, text files, and other file types
- **Material Design 3**: Modern UI following Google's Material Design guidelines
- **Internationalization**: Supports English, German, and French languages
- **Customizable**: Configurable sort order and font size settings

## Technical Specifications

- **Package Name**: com.giraone.archiver
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Build Tool**: Gradle with Kotlin DSL
- **Min SDK**: 28 (Android 9.0)
- **Target SDK**: 35 (Android 15)
- **Architecture**: MVVM with Repository pattern

## Project Structure

```
app/src/main/java/com/giraone/archiver/
├── data/
│   ├── FileItem.kt          # Data model for archived files
│   ├── FileRepository.kt    # File management operations
│   ├── PreferencesManager.kt # Settings persistence with DataStore
│   ├── FileType.kt          # File type enumeration
│   ├── SortOrder.kt         # Sort order enumeration
│   └── FontSize.kt          # Font size enumeration
├── ui/
│   ├── MainActivity.kt      # Main file list activity
│   ├── PreferenceActivity.kt # Settings screen
│   ├── HelpActivity.kt      # Help documentation
│   ├── FileDisplayActivity.kt # File viewer for images/text
│   ├── components/
│   │   ├── FileListItem.kt  # Individual file list item
│   │   └── MarkdownDisplay.kt # Markdown help renderer
│   └── theme/               # Material 3 theming
├── utils/
│   ├── ContentHandler.kt    # Intent processing and file extraction
│   └── FileUtils.kt         # File utilities and formatting
├── viewmodel/
│   └── MainViewModel.kt     # UI state management
└── ArchiverApplication.kt   # Application class
```

## Dependencies

- **androidx.compose.ui**: Jetpack Compose UI toolkit
- **androidx.compose.material3**: Material Design 3 components
- **androidx.navigation:navigation-compose**: Navigation component
- **androidx.datastore-preferences**: Settings persistence
- **androidx.lifecycle-viewmodel-compose**: ViewModel integration
- **io.noties.markwon**: Markdown rendering for help content

## Building

1. Clone this repository
2. Open in Android Studio
3. Sync project with Gradle files
4. Run the app on a device or emulator

## Usage

1. **Sharing Files**: Use the share function in any app and select "Archiver"
2. **Managing Files**: View your archived files in the main screen
3. **Context Actions**: Long-press any file for options (Display, Rename, Delete)
4. **Settings**: Access sort order and font size preferences via the settings menu
5. **Help**: View detailed help documentation in your preferred language

## File Storage

- Files are stored in `/data/data/com.giraone.archiver/files/archived_files/`
- Each file is renamed with format: `{UUID}-{original_filename}`
- Original filenames are preserved and displayed in the UI
- File metadata is extracted and cached for efficient browsing

## Privacy & Security

- All files remain on your device - no network access
- Files stored in app's private directory, inaccessible to other apps
- No data collection or sharing with third parties
- Backup rules included for proper Android backup behavior

## Testing

Unit tests are provided for:
- FileUtils (file size formatting, date formatting, type detection)
- PreferencesManager (settings persistence)
- FileRepository (CRUD operations and sorting)
- ContentHandler (intent processing and file extraction)

Run tests with: `./gradlew test`

## Internationalization

Supported languages:
- English (default)
- German (Deutsch)
- French (Français)

Help content and UI strings are fully localized.

## License

This is a sample project created for demonstration purposes.