package com.church.injilkeselamatan.core.di

import android.content.Context
import com.church.injilkeselamatan.core.R
import com.church.injilkeselamatan.core.download.DownloadListener
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.scheduler.Requirements
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DownloadModule {
    @Singleton
    @Provides
    fun provideExoplayerDatabase(@ApplicationContext context: Context): StandaloneDatabaseProvider =
        StandaloneDatabaseProvider(context)

    @Singleton
    @Provides
    fun provideHttpDataSourceFactory(@ApplicationContext context: Context): DefaultHttpDataSource.Factory =
        DefaultHttpDataSource.Factory()
            .setUserAgent(
                Util.getUserAgent(
                    context,
                    context.resources.getString(R.string.app_name)
                )
            )
            .setConnectTimeoutMs(30 * 1000)
            .setReadTimeoutMs(30 * 1000)
            .setAllowCrossProtocolRedirects(true)

    @Singleton
    @Provides
    fun provideDownloadContentDirectory(@ApplicationContext context: Context): File =
        File(context.getExternalFilesDir(null), "audiorenungan")


    @Singleton
    @Provides
    fun provideDownloadCache(
        database: StandaloneDatabaseProvider,
        downloadContentDirectory: File
    ): SimpleCache =
        SimpleCache(downloadContentDirectory, NoOpCacheEvictor(), database)

    @Singleton
    @Provides
    fun provideCachedSourceFactory(
        cache: SimpleCache,
        httpDataSourceFactory: DefaultHttpDataSource.Factory
    ): DataSource.Factory = CacheDataSource.Factory()
        .setCache(cache)
        .setUpstreamDataSourceFactory(httpDataSourceFactory)
        .setCacheWriteDataSinkFactory(null) // Disable writing.
        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

    @Singleton
    @Provides
    fun provideDownloadListener() = DownloadListener()


    @Singleton
    @Provides
    fun provideDownloadManager(
        @ApplicationContext context: Context,
        database: StandaloneDatabaseProvider,
        cache: SimpleCache,
        dataSourceFactory: DefaultHttpDataSource.Factory,
        downloadListener: DownloadListener
    ): DownloadManager = DownloadManager(
        context,
        database,
        cache,
        dataSourceFactory,
        Executors.newSingleThreadExecutor()
    ).also {
        it.requirements = Requirements(Requirements.NETWORK)
        it.maxParallelDownloads = 1
        it.addListener(downloadListener)
    }
}