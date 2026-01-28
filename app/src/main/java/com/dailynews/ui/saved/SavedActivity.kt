package com.dailynews.ui.saved

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dailynews.R
import com.dailynews.databinding.ActivitySavedBinding
import com.dailynews.ui.detail.DetailActivity
import com.dailynews.utils.Constants
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SavedActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySavedBinding
    private val viewModel: SavedViewModel by viewModels()
    private lateinit var savedArticlesAdapter: SavedArticlesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupSwipeToDelete()
        observeViewModel()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.saved_articles)
        }
    }

    private fun setupRecyclerView() {
        savedArticlesAdapter = SavedArticlesAdapter(
            onItemClick = { article ->
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra(Constants.ARTICLE_EXTRA, article)
                startActivity(intent)
            },
            onDeleteClick = { article ->
                viewModel.removeSavedArticle(article)
                Snackbar.make(
                    binding.root,
                    getString(R.string.article_deleted),
                    Snackbar.LENGTH_LONG
                ).setAction(getString(R.string.undo)) {
                    viewModel.restoreArticle(article)
                }.show()
            }
        )

        binding.rvSavedArticles.apply {
            adapter = savedArticlesAdapter
            layoutManager = LinearLayoutManager(this@SavedActivity)
        }
    }

    private fun setupSwipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val article = savedArticlesAdapter.currentList[position]

                viewModel.removeSavedArticle(article)

                Snackbar.make(
                    binding.root,
                    getString(R.string.article_deleted),
                    Snackbar.LENGTH_LONG
                ).setAction(getString(R.string.undo)) {
                    viewModel.restoreArticle(article)
                }.show()
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.rvSavedArticles)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.savedArticles.collect { articles ->
                binding.progressBar.isVisible = false

                if (articles.isEmpty()) {
                    binding.emptyStateLayout.isVisible = true
                    binding.rvSavedArticles.isVisible = false
                } else {
                    binding.emptyStateLayout.isVisible = false
                    binding.rvSavedArticles.isVisible = true
                    savedArticlesAdapter.submitList(articles)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
