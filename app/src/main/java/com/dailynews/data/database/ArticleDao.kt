// data/database/ArticleDao.kt
package com.dailynews.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.dailynews.data.model.Article

@Dao
interface ArticleDao {

    @Query("SELECT * FROM articles ORDER BY publishedAt DESC")
    fun getAllArticles(): Flow<List<Article>>

    @Query("SELECT * FROM articles WHERE isSaved = 1 ORDER BY savedAt DESC")
    fun getSavedArticles(): Flow<List<Article>>

    @Query("SELECT * FROM articles WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchArticles(query: String): Flow<List<Article>>

    @Query("SELECT * FROM articles WHERE category = :category ORDER BY publishedAt DESC")
    fun getArticlesByCategory(category: String): Flow<List<Article>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<Article>)

    @Update
    suspend fun updateArticle(article: Article)

    @Query("DELETE FROM articles WHERE isSaved = 0")
    suspend fun clearCache()

    @Query("SELECT * FROM articles WHERE url = :url")
    suspend fun getArticleByUrl(url: String): Article?
}

