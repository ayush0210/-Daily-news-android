package com.dailynews.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.dailynews.data.repository.NewsRepository
import com.dailynews.data.model.Article
import com.dailynews.utils.Resource

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    private val _articles = MutableStateFlow<Resource<List<Article>>>(Resource.Loading())
    val articles: StateFlow<Resource<List<Article>>> = _articles.asStateFlow()

    private val _selectedCategory = MutableStateFlow("general")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        getNews()
    }

    fun getNews(category: String? = null) {
        viewModelScope.launch {
            repository.getTopHeadlines(category).collect { resource ->
                _articles.value = resource
            }
        }
    }

    fun searchNews(query: String) {
        _searchQuery.value = query
        if (query.isNotEmpty()) {
            viewModelScope.launch {
                repository.searchNews(query).collect { resource ->
                    _articles.value = resource
                }
            }
        } else {
            getNews(_selectedCategory.value)
        }
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
        getNews(category)
    }

    fun saveArticle(article: Article) {
        viewModelScope.launch {
            repository.saveArticle(article)
            getNews(_selectedCategory.value)
        }
    }

    fun refresh() {
        getNews(_selectedCategory.value)
    }
}