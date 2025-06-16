package com.dailynews.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dailynews.databinding.ItemArticleBinding
import com.dailynews.data.model.Article
import com.dailynews.utils.loadImage
import com.dailynews.utils.formatDate

class NewsAdapter(
    private val onItemClick: (Article) -> Unit,
    private val onSaveClick: (Article) -> Unit
) : ListAdapter<Article, NewsAdapter.ArticleViewHolder>(ArticleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ArticleViewHolder(
        private val binding: ItemArticleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            binding.apply {
                tvTitle.text = article.title
                tvDescription.text = article.description
                tvSource.text = article.source.name
                tvPublishedAt.text = article.publishedAt.formatDate()

                ivArticle.loadImage(article.urlToImage)

                root.setOnClickListener { onItemClick(article) }
                btnSave.setOnClickListener { onSaveClick(article) }

                btnSave.setIconResource(
                    if (article.isSaved) android.R.drawable.btn_star_big_on
                    else android.R.drawable.btn_star_big_off
                )
                btnSave.iconTint = android.content.res.ColorStateList.valueOf(
                    if (article.isSaved) root.context.getColor(android.R.color.holo_blue_dark)
                    else root.context.getColor(android.R.color.darker_gray)
                )
            }
        }
    }

    private class ArticleDiffCallback : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }
}