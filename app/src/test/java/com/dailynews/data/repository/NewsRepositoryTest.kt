package com.dailynews.data.repository

import app.cash.turbine.test
import com.dailynews.data.api.NewsApiService
import com.dailynews.data.database.ArticleDao
import com.dailynews.data.model.Article
import com.dailynews.data.model.NewsResponse
import com.dailynews.data.model.Source
import com.dailynews.utils.Resource
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull

class NewsRepositoryTest {

    @Mock
    private lateinit var newsApiService: NewsApiService

    @Mock
    private lateinit var articleDao: ArticleDao

    private lateinit var repository: NewsRepository

    private val testArticle = Article(
        url = "https://test.com/article",
        title = "Test Article",
        description = "Test Description",
        urlToImage = "https://test.com/image.jpg",
        publishedAt = "2024-01-01T00:00:00Z",
        content = "Test Content",
        author = "Test Author",
        source = Source(id = "test-source", name = "Test Source"),
        category = "general",
        isSaved = false,
        savedAt = 0L
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = NewsRepository(newsApiService, articleDao)
    }

    @Test
    fun `getTopHeadlines returns success when API call succeeds`() = runTest {
        // Given
        val newsResponse = NewsResponse(
            status = "ok",
            totalResults = 1,
            articles = listOf(testArticle)
        )
        `when`(articleDao.getSavedArticles()).thenReturn(flowOf(emptyList()))
        `when`(newsApiService.getTopHeadlines(anyOrNull(), any())).thenReturn(newsResponse)

        // When
        repository.getTopHeadlines("general").test {
            // Then
            val loading = awaitItem()
            assertTrue(loading is Resource.Loading)

            val success = awaitItem()
            assertTrue(success is Resource.Success)
            assertEquals(1, success.data?.size)
            assertEquals("Test Article", success.data?.first()?.title)

            awaitComplete()
        }

        verify(articleDao).clearCache()
        verify(articleDao).insertArticles(any())
    }

    @Test
    fun `getTopHeadlines returns error when API call fails`() = runTest {
        // Given
        `when`(articleDao.getSavedArticles()).thenReturn(flowOf(emptyList()))
        `when`(newsApiService.getTopHeadlines(anyOrNull(), any()))
            .thenThrow(RuntimeException("Network error"))
        `when`(articleDao.getArticlesByCategory(any())).thenReturn(flowOf(emptyList()))

        // When
        repository.getTopHeadlines("general").test {
            // Then
            val loading = awaitItem()
            assertTrue(loading is Resource.Loading)

            val error = awaitItem()
            assertTrue(error is Resource.Error)
            assertNotNull(error.message)

            awaitComplete()
        }
    }

    @Test
    fun `getTopHeadlines returns cached data when API fails and cache exists`() = runTest {
        // Given
        val cachedArticles = listOf(testArticle.copy(title = "Cached Article"))
        `when`(articleDao.getSavedArticles()).thenReturn(flowOf(emptyList()))
        `when`(newsApiService.getTopHeadlines(anyOrNull(), any()))
            .thenThrow(RuntimeException("Network error"))
        `when`(articleDao.getArticlesByCategory(any())).thenReturn(flowOf(cachedArticles))

        // When
        repository.getTopHeadlines("general").test {
            // Then
            val loading = awaitItem()
            assertTrue(loading is Resource.Loading)

            val success = awaitItem()
            assertTrue(success is Resource.Success)
            assertEquals(1, success.data?.size)
            assertEquals("Cached Article", success.data?.first()?.title)

            awaitComplete()
        }
    }

    @Test
    fun `searchNews returns local results first`() = runTest {
        // Given
        val localResults = listOf(testArticle)
        val remoteResponse = NewsResponse(
            status = "ok",
            totalResults = 1,
            articles = listOf(testArticle.copy(title = "Remote Article"))
        )
        `when`(articleDao.searchArticles(any())).thenReturn(flowOf(localResults))
        `when`(newsApiService.searchNews(any(), any())).thenReturn(remoteResponse)

        // When
        repository.searchNews("test").test {
            // Then
            val loading = awaitItem()
            assertTrue(loading is Resource.Loading)

            val localSuccess = awaitItem()
            assertTrue(localSuccess is Resource.Success)
            assertEquals("Test Article", localSuccess.data?.first()?.title)

            val remoteSuccess = awaitItem()
            assertTrue(remoteSuccess is Resource.Success)
            assertEquals("Remote Article", remoteSuccess.data?.first()?.title)

            awaitComplete()
        }
    }

    @Test
    fun `getSavedArticles returns flow from dao`() = runTest {
        // Given
        val savedArticles = listOf(testArticle.copy(isSaved = true))
        `when`(articleDao.getSavedArticles()).thenReturn(flowOf(savedArticles))

        // When
        repository.getSavedArticles().test {
            // Then
            val result = awaitItem()
            assertEquals(1, result.size)
            assertTrue(result.first().isSaved)
            awaitComplete()
        }
    }

    @Test
    fun `saveArticle updates article with saved flag`() = runTest {
        // When
        repository.saveArticle(testArticle)

        // Then
        verify(articleDao).updateArticle(any())
    }

    @Test
    fun `removeSavedArticle updates article with unsaved flag`() = runTest {
        // When
        repository.removeSavedArticle(testArticle.copy(isSaved = true))

        // Then
        verify(articleDao).updateArticle(any())
    }

    @Test
    fun `clearCache delegates to dao`() = runTest {
        // When
        repository.clearCache()

        // Then
        verify(articleDao).clearCache()
    }

    @Test
    fun `isArticleSaved returns flow from dao`() = runTest {
        // Given
        `when`(articleDao.isArticleSaved(any())).thenReturn(flowOf(true))

        // When
        repository.isArticleSaved("test-url").test {
            // Then
            val result = awaitItem()
            assertTrue(result)
            awaitComplete()
        }
    }
}
