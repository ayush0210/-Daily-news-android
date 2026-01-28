package com.dailynews.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.dailynews.R
import com.dailynews.data.model.Article
import com.dailynews.ui.main.MainActivity
import com.dailynews.utils.Constants

object NotificationHelper {

    private const val CHANNEL_ID = "news_updates_channel"
    private const val CHANNEL_NAME = "News Updates"
    private const val NOTIFICATION_ID = 1001

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = context.getString(R.string.notification_channel_description)
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNewsNotification(
        context: Context,
        articles: List<Article>
    ) {
        if (articles.isEmpty()) return

        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Create notification based on number of articles
        when {
            articles.size == 1 -> showSingleArticleNotification(context, articles[0], pendingIntent)
            else -> showMultipleArticlesNotification(context, articles, pendingIntent)
        }
    }

    private fun showSingleArticleNotification(
        context: Context,
        article: Article,
        defaultPendingIntent: PendingIntent
    ) {
        // Create deep link intent for single article
        val deepLinkIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("dailynews://article")).apply {
            putExtra(Constants.ARTICLE_EXTRA, article)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            article.hashCode(),
            deepLinkIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(article.title)
            .setContentText(article.description ?: context.getString(R.string.check_latest_news))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(article.description ?: "")
                    .setBigContentTitle(article.title)
                    .setSummaryText(article.source.name)
            )

        // Try to load and attach image
        if (!article.urlToImage.isNullOrEmpty()) {
            try {
                Glide.with(context)
                    .asBitmap()
                    .load(article.urlToImage)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            builder.setLargeIcon(resource)
                            builder.setStyle(
                                NotificationCompat.BigPictureStyle()
                                    .bigPicture(resource)
                                    .bigLargeIcon(null as Bitmap?)
                            )
                            showNotification(context, builder)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            showNotification(context, builder)
                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            super.onLoadFailed(errorDrawable)
                            showNotification(context, builder)
                        }
                    })
            } catch (e: Exception) {
                showNotification(context, builder)
            }
        } else {
            showNotification(context, builder)
        }
    }

    private fun showMultipleArticlesNotification(
        context: Context,
        articles: List<Article>,
        pendingIntent: PendingIntent
    ) {
        val inboxStyle = NotificationCompat.InboxStyle()
            .setBigContentTitle(
                context.getString(R.string.new_articles_available, articles.size)
            )

        articles.take(5).forEach { article ->
            inboxStyle.addLine("${article.source.name}: ${article.title}")
        }

        if (articles.size > 5) {
            inboxStyle.addLine("+${articles.size - 5} more")
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.new_articles_available, articles.size))
            .setContentText(context.getString(R.string.check_latest_news))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(inboxStyle)
            .setNumber(articles.size)

        showNotification(context, builder)
    }

    private fun showNotification(
        context: Context,
        builder: NotificationCompat.Builder
    ) {
        with(NotificationManagerCompat.from(context)) {
            try {
                notify(NOTIFICATION_ID, builder.build())
            } catch (e: SecurityException) {
                // Handle case where notification permission is not granted
                e.printStackTrace()
            }
        }
    }

    fun cancelNotification(context: Context) {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
    }
}
