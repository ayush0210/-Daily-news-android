# Setup Instructions

## Prerequisites

Before you begin, ensure you have the following installed:

- **Android Studio**: Hedgehog | 2023.1.1 or later
- **JDK**: Version 17 or later
- **Android SDK**: API 34 (Android 14)
- **Minimum SDK**: API 24 (Android 7.0)
- **Git**: For version control

## Step-by-Step Setup

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/-Daily-news-android.git
cd -Daily-news-android
```

### 2. NewsAPI Configuration

1. Visit [NewsAPI.org](https://newsapi.org/register) and create a free account
2. Get your API key from the dashboard
3. Create a `local.properties` file in the root directory
4. Add your API key:
   ```properties
   NEWS_API_KEY=your_actual_api_key_here
   ```

**Note**: The free tier allows 100 requests per day. This is sufficient for development and testing.

### 3. Firebase Setup (Optional but Recommended)

Firebase provides analytics and crash reporting. To set it up:

#### Option A: Use Your Own Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or use an existing one
3. Add an Android app to your project
4. Use package name: `com.dailynews`
5. Download the `google-services.json` file
6. Place it in the `app/` directory

#### Option B: Skip Firebase (Development Only)

You can temporarily comment out Firebase plugins in `build.gradle.kts`:

```kotlin
plugins {
    // ... other plugins
    // id("com.google.gms.google-services")
    // id("com.google.firebase.crashlytics")
}
```

And remove Firebase dependencies from `app/build.gradle.kts`.

**Note**: The app will work without Firebase, but analytics and crash reporting won't be available.

### 4. Sync Project with Gradle Files

1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. If prompted, update Gradle or Android Gradle Plugin

### 5. Build and Run

#### Debug Build
```bash
./gradlew assembleDebug
```

#### Release Build
```bash
./gradlew assembleRelease
```

#### Run on Emulator/Device
1. Connect an Android device or start an emulator
2. Click the "Run" button in Android Studio
3. Or use: `./gradlew installDebug`

### 6. Run Tests

```bash
# Run all unit tests
./gradlew test

# Run specific test class
./gradlew test --tests NewsRepositoryTest

# Run instrumentation tests (requires device/emulator)
./gradlew connectedAndroidTest
```

## Common Issues and Solutions

### Issue: "API key not found"
**Solution**: Ensure `local.properties` exists and contains the correct API key.

### Issue: "google-services.json not found"
**Solution**: Either add the Firebase configuration file or comment out Firebase plugins in build files.

### Issue: Gradle sync fails
**Solution**:
- Check your internet connection
- Update Android Studio to the latest version
- Invalidate caches: File → Invalidate Caches → Restart

### Issue: Build fails with "Duplicate class"
**Solution**: Clean and rebuild:
```bash
./gradlew clean
./gradlew build
```

### Issue: Tests fail
**Solution**: Ensure you're using JDK 17 and all dependencies are up to date.

## Project Configuration

### Minimum Requirements
- **compileSdk**: 34
- **minSdk**: 24
- **targetSdk**: 34
- **JVM Target**: 1.8
- **Kotlin**: 1.9.20

### Key Dependencies
- Room: 2.6.1
- Retrofit: 2.9.0
- Hilt: 2.50
- Coroutines: 1.7.3
- Material3: 1.11.0

## Development Workflow

1. Create a feature branch: `git checkout -b feature/your-feature-name`
2. Make your changes
3. Run tests: `./gradlew test`
4. Run lint: `./gradlew lint`
5. Commit changes: `git commit -m "Add your message"`
6. Push to branch: `git push origin feature/your-feature-name`
7. Create a Pull Request

## Additional Resources

- [Android Developer Documentation](https://developer.android.com/)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Material Design 3](https://m3.material.io/)
- [NewsAPI Documentation](https://newsapi.org/docs)
- [Firebase Documentation](https://firebase.google.com/docs)

## Need Help?

If you encounter any issues:
1. Check the [GitHub Issues](https://github.com/yourusername/-Daily-news-android/issues) page
2. Create a new issue with detailed information
3. Include error messages, logs, and steps to reproduce

---

Happy coding! 🚀
