package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.player

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.common.EMPTY_PLAYBACK_STATE
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.common.NOTHING_PLAYING

data class CurrentPlayerState(
    val mediaMetadataCompat: MediaMetadataCompat = NOTHING_PLAYING,
    val songIndex: Int = 0,
    val playbackStateCompat: PlaybackStateCompat = EMPTY_PLAYBACK_STATE
)
