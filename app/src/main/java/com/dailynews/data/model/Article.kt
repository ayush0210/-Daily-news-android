// data/model/Article.kt
package com.dailynews.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Entity(tableName = "articles")
@Parcelize
data class Article(
    @PrimaryKey
    val url: String,
    val title: String,
    val description: String?,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?,
    val author: String?,
    val source: Source,
    val category: String = "general",
    val isSaved: Boolean = false,
    val savedAt: Long = 0L
) : Parcelable

@Parcelize
data class Source(
    val id: String?,
    val name: String
) : Parcelable
