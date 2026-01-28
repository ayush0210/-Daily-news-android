package com.dailynews.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dailynews.BuildConfig
import com.dailynews.data.api.NewsApiService
import com.dailynews.data.database.ArticleDao
import com.dailynews.data.model.Article
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class NewsUpdateWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val newsApiService: NewsApiService,
    private val articleDao: ArticleDao
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "news_update_work"
        private const val MAX_ARTICLES_TO_CHECK = 10
    }

    override suspend fun doWork(): Result {
        return try {
            // Get the latest articles from the API
            val response = newsApiService.getTopHeadlines(
                category = "general",
                apiKey = BuildConfig.NEWS_API_KEY
            )

            if (response.articles.isEmpty()) {
                return Result.success()
            }

            // Get existing articles from database
            val existingArticles = articleDao.getAllArticles().first()
            val existingUrls = existingArticles.map { it.url }.toSet()

            // Find new articles that don't exist in the database
            val newArticles = response.articles
                .take(MAX_ARTICLES_TO_CHECK)
                .filter { it.url !in existingUrls }

            if (newArticles.isNotEmpty()) {
                // Save new articles to database
                val articlesWithCategory = newArticles.map { article ->
                    article.copy(category = "general")
                }
                articleDao.insertArticles(articlesWithCategory)

                // Show notification for new articles
                NotificationHelper.showNewsNotification(
                    context = applicationContext,
                    articles = newArticles
                )
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            // Retry on failure, but don't fail permanently
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}
