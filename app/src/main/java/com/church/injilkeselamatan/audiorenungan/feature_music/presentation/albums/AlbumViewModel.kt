package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.albums

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.church.injilkeselamatan.audiorenungan.feature_music.data.util.Resource
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.model.Song
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.use_case.SongUseCases
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.common.MusicServiceConnection
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.common.NOTHING_PLAYING
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.PersistentStorage
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.isPrepared
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.title
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.library.UAMP_ALBUMS_ROOT
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.util.SongsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val savedSong: PersistentStorage,
    private val songUseCases: SongUseCases
) : ViewModel() {

    var mediaId by mutableStateOf(UAMP_ALBUMS_ROOT)

    private val _state = mutableStateOf(SongsState<Song>())
    val state: State<SongsState<Song>> = _state

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    sealed class UIEvent {
        data class ShowSnackbar(val message: String): UIEvent()
    }

    private var getSongsJob: Job? = null

    private val _recentSong = mutableStateOf(NOTHING_PLAYING)
    val recentSong: State<MediaMetadataCompat> = _recentSong

    private val subscriptionCallback =
        object : MediaBrowserCompat.SubscriptionCallback() {
            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>
            ) {
                super.onChildrenLoaded(parentId, children)
                Log.d(TAG, "callback: $parentId")
            }

            override fun onError(parentId: String, options: Bundle) {
                Log.d(TAG, "callback: error $parentId")
                super.onError(parentId, options)
            }
        }


    init {
        loadSongs(forceRefresh = true)
    }

    private fun loadRecentSong() {
        viewModelScope.launch {
            _recentSong.value = savedSong.loadRecentSong().first()
        }
    }

    fun playingMetadata(): StateFlow<MediaMetadataCompat> {
        return musicServiceConnection.nowPlaying
    }

    fun playbackState(): StateFlow<PlaybackStateCompat> {
        return musicServiceConnection.playbackState
    }

    fun onEvent(event: AlbumsEvent) {
        val transportControls = musicServiceConnection.transportControls
        Log.d(TAG, "NowPlaying: ${musicServiceConnection.nowPlaying.value.title}")
        when (event) {
            is AlbumsEvent.PlayOrPause -> {
                if (event.isPlay) {
                    transportControls.play()
                } else {
                    transportControls.pause()
                }
            }
        }
    }

    fun loadSongs(forceRefresh: Boolean = false) {
        getSongsJob?.cancel()
        val isPrepared = musicServiceConnection.playbackState.value.isPrepared
        getSongsJob = songUseCases.getSongs(forceRefresh = forceRefresh).onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.sortedBy { it.id }?.distinctBy { data ->
                        data.album
                    }?.let { albums ->
                        _state.value = state.value.copy(
                            songs = albums,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                    if (!isPrepared) {
                        loadRecentSong()
                        musicServiceConnection.sendCommand("connect", null)
                        musicServiceConnection.subscribe(mediaId, subscriptionCallback)
                        Log.d(TAG, "subscribe, $mediaId")
                    }
                }
                is Resource.Loading -> {
                    _state.value = state.value.copy(
                        songs = resource.data ?: emptyList(),
                        isLoading = true
                    )
                }
                is Resource.Error -> {
                    _state.value = state.value.copy(
                        songs = resource.data ?: emptyList(),
                        isLoading = false,
                        errorMessage = resource.message
                    )
                    _eventFlow.emit(UIEvent.ShowSnackbar(resource.message))
                }

            }
            if (musicServiceConnection.nowPlaying.value == NOTHING_PLAYING) {
                savedSong.loadRecentSong().first()
            }

        }.launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe(mediaId, subscriptionCallback)
    }
}

private const val TAG = "AlbumViewModel"