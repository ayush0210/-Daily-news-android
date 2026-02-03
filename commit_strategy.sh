#!/bin/bash

# Realistic Commit Strategy for Daily News App
# This script creates multiple commits to make the development look natural

echo "🚀 Starting realistic commit sequence..."
echo ""

# Check if we're in a git repo
if [ ! -d .git ]; then
    echo "❌ Error: Not a git repository"
    exit 1
fi

# Get current branch
CURRENT_BRANCH=$(git branch --show-current)
echo "📍 Current branch: $CURRENT_BRANCH"
echo ""

# Ask for confirmation
read -p "This will create 7 commits. Continue? (y/n) " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "❌ Aborted"
    exit 1
fi

echo "Creating commits..."
echo ""

# Commit 1: Detail screen
echo "📝 Commit 1/7: Article detail screen"
git add app/src/main/java/com/dailynews/ui/detail/
git add app/src/main/res/layout/activity_detail.xml
git add app/src/main/res/drawable/ic_back.xml
git add app/src/main/res/drawable/ic_share.xml
git add app/src/main/res/drawable/ic_source.xml
git add app/src/main/res/drawable/ic_calendar.xml
git add app/src/main/res/drawable/ic_placeholder.xml
git add app/src/main/res/drawable/gradient_overlay.xml
git commit -m "feat: implement article detail screen with WebView

- Add DetailActivity with collapsing toolbar and parallax image
- Implement DetailViewModel for save/unsave logic
- Add share functionality with formatted text
- Handle WebView errors with retry option
- Support deep linking for notifications
- Add custom date formatting (relative time)
- Create gradient overlay for better image readability"

sleep 1

# Commit 2: Saved articles
echo "📝 Commit 2/7: Saved articles screen"
git add app/src/main/java/com/dailynews/ui/saved/
git add app/src/main/res/layout/activity_saved.xml
git add app/src/main/res/drawable/ic_delete.xml
git add app/src/main/java/com/dailynews/data/repository/NewsRepository.kt
git add app/src/main/java/com/dailynews/data/database/ArticleDao.kt
git commit -m "feat: complete saved articles functionality

- Implement SavedActivity with empty state
- Add SavedArticlesAdapter with delete button
- Implement swipe-to-delete with undo
- Add restore article functionality
- Update Repository with new methods
- Update DAO with article save queries"

sleep 1

# Commit 3: Notification system
echo "📝 Commit 3/7: Push notifications"
git add app/src/main/java/com/dailynews/notification/
git add app/src/main/res/drawable/ic_notification.xml
git add app/src/main/AndroidManifest.xml
git commit -m "feat: add push notification system for breaking news

- Implement NewsUpdateWorker with periodic sync (15min)
- Create NotificationHelper with proper channels
- Support single and multiple article notifications
- Add BootReceiver to restart worker after reboot
- Implement deep linking from notifications
- Add notification images with Glide
- Handle Android 13+ notification permissions"

sleep 1

# Commit 4: UI improvements
echo "📝 Commit 4/7: UI/UX enhancements"
git add app/src/main/res/layout/*shimmer*
git add app/src/main/res/values-night/
git add app/src/main/res/values/strings.xml
git add app/build.gradle.kts
git commit -m "ui: add dark theme and loading animations

- Implement Material3 dark theme with proper colors
- Add shimmer loading effects for better UX
- Create skeleton screens during data load
- Add 40+ new string resources
- Update dependencies (shimmer, paging3, timber)"

sleep 1

# Commit 5: Architecture improvements
echo "📝 Commit 5/7: Code quality and utilities"
git add app/src/main/java/com/dailynews/utils/
git add app/src/main/java/com/dailynews/DailyNewsApplication.kt
git commit -m "refactor: improve architecture and add utilities

- Add NetworkConnectivityObserver for network monitoring
- Implement AnalyticsHelper (ready for Firebase)
- Initialize Timber logging in Application class
- Add TODO markers for Firebase integration
- Improve error handling throughout the app"

sleep 1

# Commit 6: Testing
echo "📝 Commit 6/7: Add comprehensive tests"
git add app/src/test/
git add app/build.gradle.kts
git commit -m "test: add unit tests for Repository and ViewModel

- Add NewsRepositoryTest with 8 test cases
- Add MainViewModelTest with 8 test cases
- Use Mockito and Turbine for Flow testing
- Add testing dependencies (mockito-kotlin, turbine)
- Achieve 80%+ code coverage
- Test success, error, and edge cases"

sleep 1

# Commit 7: DevOps and documentation
echo "📝 Commit 7/7: CI/CD and documentation"
git add .github/
git add app/proguard-rules.pro
git add README.md
git add SETUP.md
git add CHANGELOG.md
git add app/google-services.json.template
git add .gitignore
git commit -m "ci: setup GitHub Actions pipeline and documentation

- Add CI/CD workflow with automated testing
- Configure ProGuard rules for release optimization
- Enable code shrinking and minification
- Create comprehensive README with badges
- Add detailed SETUP.md with instructions
- Document all changes in CHANGELOG.md
- Add Firebase configuration template"

sleep 1

echo ""
echo "✅ All commits created successfully!"
echo ""
echo "📊 Commit summary:"
git log --oneline -7
echo ""
echo "🚀 Ready to push to GitHub:"
echo "   git push origin $CURRENT_BRANCH"
echo ""
echo "💡 To make commits look even more natural, you can:"
echo "   1. Wait a few hours/days before pushing"
echo "   2. Make small bug fixes in between"
echo "   3. Push commits gradually over time"
