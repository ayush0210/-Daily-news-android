package com.dailynews.ui.detail

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.dailynews.R
import com.dailynews.data.model.Article
import com.dailynews.databinding.ActivityDetailBinding
import com.dailynews.utils.Constants
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels()
    private var currentArticle: Article? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()

        currentArticle = intent.getParcelableExtra(Constants.ARTICLE_EXTRA)
        currentArticle?.let { article ->
            displayArticle(article)
            setupWebView(article.url)
            checkIfSaved(article)
        }

        setupButtons()
        observeViewModel()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = ""
        }
        binding.collapsingToolbar.title = ""
    }

    private fun displayArticle(article: Article) {
        binding.apply {
            // Load article image with Glide
            Glide.with(this@DetailActivity)
                .load(article.urlToImage)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivArticleImage)

            // Set article details
            tvArticleTitle.text = article.title
            tvSource.text = article.source.name
            tvPublishedAt.text = formatDate(article.publishedAt)

            // Set author with proper handling
            if (article.author.isNullOrEmpty()) {
                tvAuthor.isVisible = false
            } else {
                tvAuthor.isVisible = true
                tvAuthor.text = getString(R.string.by_author, article.author)
            }

            // Set description
            if (article.description.isNullOrEmpty()) {
                tvDescription.text = getString(R.string.no_description_available)
            } else {
                tvDescription.text = article.description
            }
        }
    }

    private fun setupWebView(url: String) {
        binding.webView.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = true
                displayZoomControls = false
                setSupportZoom(true)
            }

            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    binding.webViewProgress.isVisible = true
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    binding.webViewProgress.isVisible = false
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    binding.webViewProgress.isVisible = false
                    Snackbar.make(
                        binding.root,
                        getString(R.string.error_loading_article),
                        Snackbar.LENGTH_LONG
                    ).setAction(getString(R.string.retry)) {
                        loadUrl(url)
                    }.show()
                }
            }

            loadUrl(url)
        }
    }

    private fun setupButtons() {
        binding.fabShare.setOnClickListener {
            shareArticle()
        }

        binding.fabSave.setOnClickListener {
            currentArticle?.let { article ->
                viewModel.toggleSaveArticle(article)
            }
        }
    }

    private fun checkIfSaved(article: Article) {
        lifecycleScope.launch {
            viewModel.isArticleSaved(article.url).collect { isSaved ->
                updateSaveButton(isSaved)
            }
        }
    }

    private fun updateSaveButton(isSaved: Boolean) {
        binding.fabSave.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                if (isSaved) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark_border
            )
        )
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.saveState.collect { state ->
                when (state) {
                    is DetailViewModel.SaveState.Saved -> {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.article_saved),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    is DetailViewModel.SaveState.Removed -> {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.article_removed),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    is DetailViewModel.SaveState.Error -> {
                        Snackbar.make(
                            binding.root,
                            state.message,
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun shareArticle() {
        currentArticle?.let { article ->
            val shareText = """
                ${article.title}

                ${article.description ?: ""}

                Read more: ${article.url}

                Shared via Daily News App
            """.trimIndent()

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                putExtra(Intent.EXTRA_SUBJECT, article.title)
                type = "text/plain"
            }

            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_article)))
        }
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(dateString)

            val now = System.currentTimeMillis()
            val diff = now - (date?.time ?: now)

            when {
                diff < 60000 -> getString(R.string.just_now)
                diff < 3600000 -> getString(R.string.minutes_ago, diff / 60000)
                diff < 86400000 -> getString(R.string.hours_ago, diff / 3600000)
                diff < 604800000 -> getString(R.string.days_ago, diff / 86400000)
                else -> {
                    val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    outputFormat.format(date ?: Date())
                }
            }
        } catch (e: Exception) {
            dateString
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

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
