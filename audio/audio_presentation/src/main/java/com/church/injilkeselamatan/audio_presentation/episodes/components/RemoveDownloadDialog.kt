package com.church.injilkeselamatan.audio_presentation.episodes.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun RemoveDownloadDialog(
    isVisible: Boolean,
    onRemoveClicked: () -> Unit,
    onDismissClicked: () -> Unit
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = { onDismissClicked() },
            shape = RoundedCornerShape(15.dp),
            title = {
                Text(
                    text = "Hapus Audio Ini?",
                    color = MaterialTheme.colors.onBackground,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Column {
                    Button(
                        onClick = { onRemoveClicked() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text(text = "Hapus", color = Color.White)
                    }
                    Button(
                        onClick = { onDismissClicked() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text(text = "Batal", color = Color.White)
                    }
                }
            }
        )
    }
}