// ui/main/MainActivity.kt
package com.dailynews.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.dailynews.R
import com.dailynews.databinding.ActivityMainBinding
import com.dailynews.notification.NewsUpdateWorker
import com.dailynews.ui.detail.DetailActivity
import com.dailynews.ui.saved.SavedActivity
import com.dailynews.utils.Constants
import com.dailynews.utils.Resource
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var newsAdapter: NewsAdapter

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startNewsUpdates()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupCategoryChips()
        setupSwipeRefresh()
        setupSavedButton()
        observeViewModel()
        checkNotificationPermission()
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    startNewsUpdates()
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            startNewsUpdates()
        }
    }

    private fun startNewsUpdates() {
        val workRequest = PeriodicWorkRequestBuilder<NewsUpdateWorker>(
            15, TimeUnit.MINUTES,
            5, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "news_updates",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Daily News"
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter(
            onItemClick = { article ->
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra(Constants.ARTICLE_EXTRA, article)
                startActivity(intent)
            },
            onSaveClick = { article ->
                viewModel.saveArticle(article)
                Snackbar.make(binding.root, "Article saved!", Snackbar.LENGTH_SHORT).show()
            }
        )

        binding.rvNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupCategoryChips() {
        Constants.NEWS_CATEGORIES.forEach { category ->
            val chip = Chip(this)
            chip.text = category.replaceFirstChar { it.uppercase() }
            chip.isCheckable = true
            chip.setOnClickListener {
                binding.chipGroup.check(chip.id)
                viewModel.selectCategory(category)
            }
            binding.chipGroup.addView(chip)

            if (category == "general") {
                chip.isChecked = true
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun setupSavedButton() {
        binding.fabSaved.setOnClickListener {
            startActivity(Intent(this, SavedActivity::class.java))
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.articles.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        binding.progressBar.isVisible = true
                        binding.swipeRefresh.isRefreshing = false
                    }
                    is Resource.Success -> {
                        binding.progressBar.isVisible = false
                        binding.swipeRefresh.isRefreshing = false
                        resource.data?.let { articles ->
                            newsAdapter.submitList(articles)
                        }
                    }
                    is Resource.Error -> {
                        binding.progressBar.isVisible = false
                        binding.swipeRefresh.isRefreshing = false
                        Snackbar.make(
                            binding.root,
                            resource.message ?: "Unknown error occurred",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchNews(it) }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    viewModel.searchNews("")
                }
                return false
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_saved -> {
                startActivity(Intent(this, SavedActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}