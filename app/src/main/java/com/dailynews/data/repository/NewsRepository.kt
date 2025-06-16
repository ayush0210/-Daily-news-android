// data/repository/NewsRepository.kt
package com.dailynews.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import com.dailynews.data.api.NewsApiService
import com.dailynews.data.database.ArticleDao
import com.dailynews.data.model.Article
import com.dailynews.utils.Resource
import com.dailynews.BuildConfig

@Singleton
class NewsRepository @Inject constructor(
    private val newsApiService: NewsApiService,
    private val articleDao: ArticleDao
) {

    fun getTopHeadlines(category: String? = null): Flow<Resource<List<Article>>> = flow {
        emit(Resource.Loading())

        try {
            // Get existing saved articles before clearing cache
            val savedArticles = articleDao.getSavedArticles().first()
            val savedUrls = savedArticles.map { it.url }.toSet()

            // Clear cache before fetching new data
            articleDao.clearCache()

            // Fetch fresh data
            val response = newsApiService.getTopHeadlines(
                category = category,
                apiKey = BuildConfig.NEWS_API_KEY
            )

            val articlesWithCategory = response.articles.map { article ->
                article.copy(
                    category = category ?: "general",
                    isSaved = article.url in savedUrls,
                    savedAt = if (article.url in savedUrls) savedArticles.find { it.url == article.url }?.savedAt ?: 0L else 0L
                )
            }

            articleDao.insertArticles(articlesWithCategory)
            emit(Resource.Success(articlesWithCategory))

        } catch (e: Exception) {
            // If network fails, try to get cached data
            val cachedArticles = if (category != null) {
                articleDao.getArticlesByCategory(category).first()
            } else {
                articleDao.getAllArticles().first()
            }

            if (cachedArticles.isNotEmpty()) {
                emit(Resource.Success(cachedArticles))
            } else {
                emit(Resource.Error(e.localizedMessage ?: "Unknown error occurred"))
            }
        }
    }

    fun searchNews(query: String): Flow<Resource<List<Article>>> = flow {
        emit(Resource.Loading())

        try {
            // First check local database
            val localResults = articleDao.searchArticles(query).first()
            if (localResults.isNotEmpty()) {
                emit(Resource.Success(localResults))
            }

            // Then search online
            val response = newsApiService.searchNews(
                query = query,
                apiKey = BuildConfig.NEWS_API_KEY
            )

            emit(Resource.Success(response.articles))

        } catch (e: Exception) {
            // Fallback to local search
            val localResults = articleDao.searchArticles(query).first()
            if (localResults.isNotEmpty()) {
                emit(Resource.Success(localResults))
            } else {
                emit(Resource.Error(e.localizedMessage ?: "Search failed"))
            }
        }
    }

    fun getSavedArticles(): Flow<List<Article>> = articleDao.getSavedArticles()

    suspend fun saveArticle(article: Article) {
        val updatedArticle = article.copy(
            isSaved = true,
            savedAt = System.currentTimeMillis()
        )
        articleDao.updateArticle(updatedArticle)
    }

    suspend fun removeSavedArticle(article: Article) {
        val updatedArticle = article.copy(
            isSaved = false,
            savedAt = 0L
        )
        articleDao.updateArticle(updatedArticle)
    }

    suspend fun clearCache() {
        articleDao.clearCache()
    }
}