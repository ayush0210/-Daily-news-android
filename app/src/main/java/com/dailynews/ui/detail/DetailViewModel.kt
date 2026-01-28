package com.dailynews.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailynews.data.model.Article
import com.dailynews.data.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    fun toggleSaveArticle(article: Article) {
        viewModelScope.launch {
            try {
                repository.isArticleSaved(article.url).collect { isSaved ->
                    if (isSaved) {
                        repository.deleteArticle(article)
                        _saveState.value = SaveState.Removed
                    } else {
                        val savedArticle = article.copy(isSaved = true, savedAt = System.currentTimeMillis())
                        repository.insertArticle(savedArticle)
                        _saveState.value = SaveState.Saved
                    }
                }
            } catch (e: Exception) {
                _saveState.value = SaveState.Error(e.message ?: "Failed to save article")
            }
        }
    }

    fun isArticleSaved(url: String): Flow<Boolean> {
        return repository.isArticleSaved(url)
    }

    sealed class SaveState {
        object Idle : SaveState()
        object Saved : SaveState()
        object Removed : SaveState()
        data class Error(val message: String) : SaveState()
    }
}
