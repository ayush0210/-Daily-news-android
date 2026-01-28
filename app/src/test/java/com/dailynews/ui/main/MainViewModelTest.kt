package com.dailynews.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.dailynews.data.model.Article
import com.dailynews.data.model.Source
import com.dailynews.data.repository.NewsRepository
import com.dailynews.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: NewsRepository

    private lateinit var viewModel: MainViewModel

    private val testDispatcher = StandardTestDispatcher()

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
        Dispatchers.setMain(testDispatcher)
        viewModel = MainViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `selectCategory updates articles flow with new category`() = runTest {
        // Given
        val categoryArticles = listOf(testArticle.copy(category = "technology"))
        `when`(repository.getTopHeadlines("technology"))
            .thenReturn(flowOf(Resource.Success(categoryArticles)))

        // When
        viewModel.selectCategory("technology")
        advanceUntilIdle()

        // Then
        viewModel.articles.test {
            val result = awaitItem()
            assertTrue(result is Resource.Success)
            assertEquals(1, result.data?.size)
            assertEquals("technology", result.data?.first()?.category)
        }
    }

    @Test
    fun `refresh reloads current category`() = runTest {
        // Given
        val articles = listOf(testArticle)
        `when`(repository.getTopHeadlines("general"))
            .thenReturn(flowOf(Resource.Success(articles)))

        // When
        viewModel.refresh()
        advanceUntilIdle()

        // Then
        verify(repository).getTopHeadlines("general")
    }

    @Test
    fun `searchNews with empty query loads all articles`() = runTest {
        // Given
        val articles = listOf(testArticle)
        `when`(repository.getTopHeadlines("general"))
            .thenReturn(flowOf(Resource.Success(articles)))

        // When
        viewModel.searchNews("")
        advanceUntilIdle()

        // Then
        verify(repository).getTopHeadlines("general")
    }

    @Test
    fun `searchNews with query performs search`() = runTest {
        // Given
        val searchResults = listOf(testArticle.copy(title = "Search Result"))
        `when`(repository.searchNews("test query"))
            .thenReturn(flowOf(Resource.Success(searchResults)))

        // When
        viewModel.searchNews("test query")
        advanceUntilIdle()

        // Then
        viewModel.articles.test {
            val result = awaitItem()
            assertTrue(result is Resource.Success)
            assertEquals("Search Result", result.data?.first()?.title)
        }
    }

    @Test
    fun `saveArticle calls repository`() = runTest {
        // When
        viewModel.saveArticle(testArticle)
        advanceUntilIdle()

        // Then
        verify(repository).saveArticle(testArticle)
    }

    @Test
    fun `articles flow starts with general category`() = runTest {
        // Given
        val articles = listOf(testArticle)
        `when`(repository.getTopHeadlines("general"))
            .thenReturn(flowOf(Resource.Success(articles)))

        // Create a new ViewModel to test initialization
        val newViewModel = MainViewModel(repository)
        advanceUntilIdle()

        // Then
        newViewModel.articles.test {
            val result = awaitItem()
            assertTrue(result is Resource.Success)
            assertEquals("general", result.data?.first()?.category)
        }
    }

    @Test
    fun `articles flow emits loading state`() = runTest {
        // Given
        `when`(repository.getTopHeadlines(any()))
            .thenReturn(flowOf(Resource.Loading()))

        // When
        viewModel.selectCategory("business")
        advanceUntilIdle()

        // Then
        viewModel.articles.test {
            val result = awaitItem()
            assertTrue(result is Resource.Loading)
        }
    }

    @Test
    fun `articles flow emits error state`() = runTest {
        // Given
        `when`(repository.getTopHeadlines(any()))
            .thenReturn(flowOf(Resource.Error("Network error")))

        // When
        viewModel.selectCategory("sports")
        advanceUntilIdle()

        // Then
        viewModel.articles.test {
            val result = awaitItem()
            assertTrue(result is Resource.Error)
            assertEquals("Network error", result.message)
        }
    }
}
