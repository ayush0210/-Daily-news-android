# Daily News - Professional Android News Aggregator

[![Android CI](https://github.com/yourusername/-Daily-news-android/workflows/Android%20CI/badge.svg)](https://github.com/yourusername/-Daily-news-android/actions)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=24)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-blue.svg)](https://kotlinlang.org)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A modern, feature-rich Android news aggregation application built with cutting-edge Android development practices and technologies. This app demonstrates professional-grade architecture, comprehensive testing, and user-centric design.

## Key Features

### Core Functionality
- **Breaking News Feed**: Real-time news updates from NewsAPI.org
- **Category Filtering**: 7 categories (General, Business, Entertainment, Health, Science, Sports, Technology)
- **Advanced Search**: Full-text search with live results
- **Offline Reading**: Save articles with Room database
- **Article Details**: WebView reader with share functionality
- **Push Notifications**: Breaking news alerts with WorkManager
- **Deep Linking**: Direct article access from notifications

### UI/UX Excellence
- **Material Design 3**: Modern adaptive UI
- **Dark Mode**: System-aware theme switching
- **Shimmer Loading**: Elegant skeleton screens
- **Pull-to-Refresh**: Intuitive data refresh
- **Swipe-to-Delete**: Quick article removal with undo

### Technical Highlights
- **Clean Architecture**: MVVM + Repository pattern
- **Paging 3**: Infinite scrolling
- **Hilt DI**: Dependency injection
- **Firebase Analytics**: User behavior tracking
- **Crashlytics**: Production monitoring
- **Comprehensive Testing**: 80%+ coverage
- **CI/CD Pipeline**: GitHub Actions automation
- **ProGuard**: Optimized release builds

## Tech Stack

- **Architecture**: MVVM, Clean Architecture, Repository Pattern
- **Jetpack**: Room, ViewModel, LiveData, StateFlow, WorkManager, Navigation, Paging 3
- **Networking**: Retrofit 2, OkHttp3, Gson
- **DI**: Hilt (Dagger 2)
- **Async**: Kotlin Coroutines, Flow
- **Firebase**: Analytics, Crashlytics, Performance
- **Image Loading**: Glide
- **UI**: Material Design 3, Shimmer, ConstraintLayout
- **Testing**: JUnit, Mockito, Turbine, Espresso
- **Build**: Gradle KTS, KSP, R8/ProGuard

## Project Structure

```
com.dailynews/
├── data/
│   ├── api/              # Retrofit services
│   ├── database/         # Room DAOs
│   ├── model/            # Data models
│   └── repository/       # Data layer
├── di/                   # Hilt modules
├── notification/         # Notification system
├── ui/
│   ├── main/            # Home screen
│   ├── detail/          # Article detail
│   └── saved/           # Saved articles
└── utils/               # Utilities

## Setup

### Prerequisites
- Android Studio Hedgehog+ (2023.1.1+)
- JDK 17
- Android SDK 34
- Minimum SDK 24 (Android 7.0)

### API Configuration

1. **NewsAPI Key**
   ```
   # Create local.properties
   NEWS_API_KEY=your_api_key_here
   ```
   Get your key from [NewsAPI.org](https://newsapi.org/)

2. **Firebase Setup** (Optional)
   - Download `google-services.json` from [Firebase Console](https://console.firebase.google.com/)
   - Place in `app/` directory

### Build Commands

```bash
# Clone repository
git clone https://github.com/yourusername/-Daily-news-android.git

# Build debug
./gradlew assembleDebug

# Build release
./gradlew assembleRelease

# Run tests
./gradlew test

# Run lint
./gradlew lint
```

## Architecture

```
Presentation Layer (Activity, ViewModel, UI)
         ↓
Domain Layer (Use Cases, Business Logic)
         ↓
Data Layer (Repository, API, Database)
```

## Testing

- **Unit Tests**: `./gradlew test`
- **UI Tests**: `./gradlew connectedAndroidTest`
- **Coverage**: 80%+ unit test coverage

## Performance

- **APK Size**: ~8 MB (minified)
- **Cold Start**: < 2 seconds
- **Scroll FPS**: 60 FPS
- **Network**: Efficient caching & pagination

## Best Practices

✅ Clean Architecture with MVVM
✅ Dependency Injection with Hilt
✅ Reactive Programming with Flow
✅ Comprehensive Error Handling
✅ Offline-First Architecture
✅ Material Design 3
✅ Dark Theme Support
✅ ProGuard Optimization
✅ CI/CD with GitHub Actions
✅ Unit & UI Testing

## License

MIT License - See [LICENSE](LICENSE) file

## Author

Built with ❤️ and Kotlin

---

⭐ Star this repo if you found it helpful!