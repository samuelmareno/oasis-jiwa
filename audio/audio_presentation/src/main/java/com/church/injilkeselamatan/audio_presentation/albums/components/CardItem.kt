package com.church.injilkeselamatan.audio_presentation.albums.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.church.injilkeselamatan.audio_domain.model.Song
import com.church.injilkeselamatan.audio_presentation.R
import com.church.injilkeselamatan.audiorenungan.core_ui.Dimensions
import com.church.injilkeselamatan.audiorenungan.core_ui.MediaQuery
import com.church.injilkeselamatan.audiorenungan.core_ui.greaterThan
import com.church.injilkeselamatan.audiorenungan.core_ui.lessThan
import com.church.injilkeselamatan.audiorenungan.core_ui.sourceSansPro

@Composable
fun CardItem(
    song: Song,
    modifier: Modifier = Modifier,
    onCardClicked: (Song) -> Unit
) {
    val painter =
        when (song.album) {
            "Pohon Kehidupan" -> R.drawable.pohon_kehidupan
            "Saat Teduh Bersama Tuhan" -> R.drawable.stbt
            else -> R.drawable.btat
        }

    MediaQuery(comparator = Dimensions.Height greaterThan 600.dp) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(150.dp)
                .clickable {
                    onCardClicked(song)
                },
            shape = RoundedCornerShape(CornerSize(12.dp)),
            elevation = 8.dp
        ) {
            Image(
                painter = painterResource(id = painter),
                contentScale = ContentScale.FillBounds,
                contentDescription = null
            )
            Box(
                contentAlignment = Alignment.BottomStart,
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Black
                            ),
                            startY = 0.0f,
                            endY = 1010f
                        )
                    )
            ) {
                Text(
                    text = song.album,
                    fontSize = 24.sp,
                    maxLines = 1,
                    fontFamily = sourceSansPro,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }

    MediaQuery(comparator = Dimensions.Height lessThan 600.dp) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(150.dp)
                .clickable {
                    onCardClicked(song)
                },
            shape = RoundedCornerShape(CornerSize(12.dp)),
            elevation = 8.dp
        ) {
            Image(
                painter = painterResource(id = painter),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
            Box(
                contentAlignment = Alignment.BottomStart,
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Black
                            ),
                            startY = 0.0f,
                            endY = 1010f
                        )
                    )
            ) {
                Text(
                    text = song.album,
                    fontSize = 24.sp,
                    maxLines = 1,
                    fontFamily = sourceSansPro,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}