package com.church.injilkeselamatan.audiorenungan.di

import android.content.Context
import android.os.Environment
import com.church.injilkeselamatan.audiorenungan.R
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.upstream.DataSink
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import dagger.hilt.components.SingletonComponent
import java.io.File
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DownloadModule {
    @Singleton
    @Provides
    fun provideExoplayerDatabase(@ApplicationContext context: Context): ExoDatabaseProvider =
        ExoDatabaseProvider(context)

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
        database: ExoDatabaseProvider,
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
    fun provideDownloadManager(
        @ApplicationContext context: Context,
        database: ExoDatabaseProvider,
        cache: SimpleCache,
        dataSourceFactory: DefaultHttpDataSource.Factory
    ): DownloadManager =
        DownloadManager(context, database, cache, dataSourceFactory, Runnable::run)
}