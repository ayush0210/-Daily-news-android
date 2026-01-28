package com.dailynews

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
// TODO: Uncomment Firebase imports after adding google-services.json
// import com.google.firebase.analytics.FirebaseAnalytics
// import com.google.firebase.analytics.ktx.analytics
// import com.google.firebase.crashlytics.FirebaseCrashlytics
// import com.google.firebase.ktx.Firebase
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class DailyNewsApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    // private lateinit var analytics: FirebaseAnalytics

    override fun onCreate() {
        super.onCreate()

        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // TODO: Enable Firebase after adding google-services.json
        // Initialize Firebase Analytics
//        try {
//            analytics = Firebase.analytics
//            Timber.d("Firebase Analytics initialized")
//        } catch (e: Exception) {
//            Timber.e(e, "Failed to initialize Firebase Analytics")
//        }
//
//        // Initialize Firebase Crashlytics
//        try {
//            FirebaseCrashlytics.getInstance().apply {
//                setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
//            }
//            Timber.d("Firebase Crashlytics initialized")
//        } catch (e: Exception) {
//            Timber.e(e, "Failed to initialize Firebase Crashlytics")
//        }

        Timber.i("DailyNewsApplication initialized successfully")
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}