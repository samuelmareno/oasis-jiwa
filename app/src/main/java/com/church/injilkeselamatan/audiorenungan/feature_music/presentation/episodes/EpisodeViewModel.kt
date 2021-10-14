package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.episodes

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.remote.models.MediaItemData
import com.church.injilkeselamatan.audiorenungan.feature_music.data.util.Resource
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.use_case.SongUseCases
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.common.MusicServiceConnection
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.id
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.isPlayEnabled
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.isPlaying
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.isPrepared
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.util.SongsState
import com.google.android.exoplayer2.offline.DownloadManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EpisodeViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val songUseCases: SongUseCases,
    private val downloadManager: DownloadManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    var downloadedLength = MutableLiveData(0)
    var maxProgress = MutableLiveData(0)

    private val _state = mutableStateOf(SongsState())
    val state: State<SongsState> = _state

    private var job: Job? = null

    private var currentMediaId: String? = null

    val playbackStateCompat = musicServiceConnection.playbackState
    val mediaMetadataCompat = musicServiceConnection.nowPlaying

    private val subscriptionCallback =
        object : MediaBrowserCompat.SubscriptionCallback() {
        }

    init {
        savedStateHandle.get<String>("album")?.let { album ->
            currentMediaId = album
        }
        loadEpisodes()

    }

    fun loadEpisodes() {
        job?.cancel()
        job = songUseCases.getSongs(currentMediaId).onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let { episodes ->
                        _state.value = state.value.copy(
                            songs = episodes,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                    Log.d(TAG, "Success")
                }
                is Resource.Loading -> {
                    _state.value = state.value.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                    Log.d(TAG, "Loading")
                }
                is Resource.Error -> {
                    _state.value = state.value.copy(
                        isLoading = false,
                        errorMessage = resource.message
                    )
                    Log.d(TAG, "Error")
                }
            }
        }.launchIn(viewModelScope)
    }

    fun onEvent() {

    }

    fun maxProgressDownload(title: String) {
        viewModelScope.launch {
            val download = downloadManager.currentDownloads.find {
                it.request.id == title
            }
            delay(500L)
            Log.d("EpisodeViewModel", download?.request?.id ?: "Download still empty")
            while (true) {
                maxProgress.postValue(download?.contentLength?.toInt())
                downloadedLength.postValue(download?.contentLength?.toInt())
                delay(200)
            }
        }
    }

    fun playMedia(mediaItem: MediaItemData, pauseAllowed: Boolean = true) {
        val nowPlaying = musicServiceConnection.nowPlaying.value

        val transportControls = musicServiceConnection.transportControls
        val isPrepared = musicServiceConnection.playbackState.value?.isPrepared ?: false
        if (isPrepared && mediaItem.mediaId == nowPlaying?.id) {
            musicServiceConnection.playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying ->
                        if (pauseAllowed) transportControls.pause() else Unit
                    playbackState.isPlayEnabled -> transportControls.play()
                    else -> {
                        // Something wrong
                    }
                }
            }
        } else {
            transportControls.playFromMediaId(mediaItem.mediaId, null)
        }
    }

    fun playMediaId(mediaId: String) {

        val nowPlaying = musicServiceConnection.nowPlaying.value

        val transportControls = musicServiceConnection.transportControls
        val isPrepared = musicServiceConnection.playbackState.value?.isPrepared ?: false
        if (isPrepared && mediaId == nowPlaying?.id) {
            musicServiceConnection.playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying -> transportControls.pause()
                    playbackState.isPlayEnabled -> transportControls.play()
                    else -> {
                        // Something wrong
                    }
                }
            }
        } else {
            transportControls.playFromMediaId(mediaId, null)
        }
    }

    fun downloadSong(song: String) {
        val bundle = Bundle()
        bundle.putString(MEDIA_METADATA_COMPAT_FOR_DOWNLOAD, song)
        musicServiceConnection.sendCommand("download_song", bundle)
    }
}

private const val TAG = "EpisodeViewModel"
const val MEDIA_METADATA_COMPAT_FOR_DOWNLOAD =
    "com.church.injilkeselamatan.audiorenungan.bundles.mediametadata"