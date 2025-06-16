package com.dailynews.ui.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.dailynews.data.repository.NewsRepository
import com.dailynews.data.model.Article

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    val savedArticles: StateFlow<List<Article>> = repository.getSavedArticles()
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun removeSavedArticle(article: Article) {
        viewModelScope.launch {
            repository.removeSavedArticle(article)
        }
    }
}