package com.church.injilkeselamatan.audio_presentation.albums.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.church.injilkeselamatan.audio_domain.model.Song
import com.church.injilkeselamatan.audiorenungan.core_ui.Dimensions
import com.church.injilkeselamatan.audiorenungan.core_ui.lessThan
import com.church.injilkeselamatan.audiorenungan.core_ui.mediaQuery

@Composable
fun CategoriesSection(
    cardItems: List<Song>,
    modifier: Modifier = Modifier,
    onNavigationClick: (Song) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        modifier = modifier
    ) {
        items(cardItems.size) {
            CardItem(cardItems[it]) { category ->
                onNavigationClick(category)
            }
            if (it != cardItems.size - 1) {
                Spacer(modifier = Modifier.padding(bottom = 16.dp))
            }
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .mediaQuery(
                        Dimensions.Height lessThan 600.dp,
                        Modifier.height(80.dp)
                    )
            )
        }
    }
}
