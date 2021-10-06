package com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.download

import android.app.Notification
import android.graphics.Color
import android.util.Log
import androidx.core.app.NotificationCompat
import com.church.injilkeselamatan.audiorenungan.R
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.scheduler.PlatformScheduler
import com.google.android.exoplayer2.scheduler.Scheduler
import com.google.android.exoplayer2.ui.DownloadNotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

const val DOWNLOAD_NOTIFICATION_ID = 2
const val DOWNLOAD_CHANNEL_ID = "com.church.injilkeselamatan.audiorenungan.DOWNLOADING"

@AndroidEntryPoint
class AudioDownloadService : DownloadService
    (
    DOWNLOAD_NOTIFICATION_ID,
    DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
    DOWNLOAD_CHANNEL_ID,
    R.string.notification_channel_download,
    R.string.notification_description_download
) {

    @Inject
    lateinit var downloadMananger: DownloadManager


    private val downloadListener = object : DownloadManager.Listener {
        override fun onDownloadChanged(
            downloadManager: DownloadManager,
            download: Download,
            finalException: Exception?
        ) {
            Log.e("AudioDownloadManager", "download ID: ${download.request.id}")
        }

        override fun onDownloadsPausedChanged(
            downloadManager: DownloadManager,
            downloadsPaused: Boolean
        ) {
            Log.e("AudioDownloadManager", "onPause: $downloadsPaused")
            super.onDownloadsPausedChanged(downloadManager, downloadsPaused)
        }
    }

    override fun getDownloadManager(): DownloadManager {
        downloadMananger.addListener(downloadListener)
        downloadMananger.maxParallelDownloads = 1
        Log.e("AudioDownloadManager", "getDownloadManager")
        return downloadMananger
    }

    override fun getScheduler(): Scheduler {
        return PlatformScheduler(this, 1)
    }

    override fun getForegroundNotification(downloads: MutableList<Download>): Notification {

        val currentDownloads = downloadMananger.currentDownloads

        if (currentDownloads.size <= 0) {
            return DownloadNotificationHelper(this, DOWNLOAD_CHANNEL_ID).buildProgressNotification(
                this,
                R.drawable.download,
                null,
                null,
                downloads
            )
        } else {
//            val currentDownload = Util.fromUtf8Bytes(downloads[0].request.data)
//            val jsonObject = JSONObject(currentDownload)
//            val metadata = jsonObject.get() as MediaMetadataCompat
            val notification = NotificationCompat.Builder(this, DOWNLOAD_CHANNEL_ID)
                .setOngoing(true)
                .setShowWhen(false)
                .setColor(Color.BLUE)
                .setSmallIcon(R.drawable.download)
                .setContentTitle("Mengunduh Audio Renungan")
                .setContentText(percentageFromFloat(currentDownloads[0].percentDownloaded))
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .setBigContentTitle("Mengunduh Audio Renungan")
                        .bigText(
                            "${currentDownloads[0].request.customCacheKey}\n${
                                percentageFromFloat(
                                    currentDownloads[0].percentDownloaded
                                )
                            }"
                        )
                )
                .setProgress(
                    currentDownloads[0].contentLength.toInt(),
                    currentDownloads[0].bytesDownloaded.toInt(),
                    false
                ) // FIXME: 9/22/2021 : baru coba false

            return notification.build()
        }
    }

    override fun onDestroy() {
        downloadMananger.removeListener(downloadListener)
        super.onDestroy()
    }

    private fun percentageFromFloat(fl: Float): String {
        return String.format(Locale.US, "%d%%", (fl * 1).toInt())
    }

    private fun getDownloadedMedia(): List<MediaItem> {
        val mediaItems: MutableList<MediaItem> = mutableListOf()

        val downloadCursor = downloadMananger.downloadIndex.getDownloads()

        if (downloadCursor.moveToFirst()) {
            do {
                val mediaItem = downloadCursor.download.request.toMediaItem()
                mediaItems.add(mediaItem)
            } while (downloadCursor.moveToNext())
        }

        return mediaItems
    }
}