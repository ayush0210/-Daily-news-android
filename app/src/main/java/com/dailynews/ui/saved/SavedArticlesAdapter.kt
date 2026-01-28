package com.dailynews.ui.saved

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dailynews.R
import com.dailynews.databinding.ItemArticleBinding
import com.dailynews.data.model.Article
import com.dailynews.utils.loadImage
import com.dailynews.utils.formatDate

class SavedArticlesAdapter(
    private val onItemClick: (Article) -> Unit,
    private val onDeleteClick: (Article) -> Unit
) : ListAdapter<Article, SavedArticlesAdapter.SavedArticleViewHolder>(ArticleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedArticleViewHolder {
        val binding = ItemArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SavedArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavedArticleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SavedArticleViewHolder(
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

                // Set delete icon for saved articles
                btnSave.setIconResource(R.drawable.ic_delete)
                btnSave.iconTint = android.content.res.ColorStateList.valueOf(
                    root.context.getColor(android.R.color.holo_red_dark)
                )
                btnSave.setOnClickListener { onDeleteClick(article) }
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
