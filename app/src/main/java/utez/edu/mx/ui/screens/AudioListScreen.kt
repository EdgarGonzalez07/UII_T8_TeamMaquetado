package utez.edu.mx.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import utez.edu.mx.ui.AudioCard
import utez.edu.mx.ui.utils.formatDate
import utez.edu.mx.viewmodel.MediaView
import utez.edu.mx.viewmodel.PlaybackView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioListScreen(
    mediaView: MediaView,
    playbackView: PlaybackView
) {
    val audioList by mediaView.allAudio.collectAsState()
    val isPlaying by playbackView.isPlaying.collectAsState()
    val isAccelerometerOn by playbackView.isAccelerometerEnabled.collectAsState()
    val currentVolume by playbackView.currentVolume.collectAsState()

    var currentlyPlayingUri by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "AUDIOS",
                    fontWeight = FontWeight.Bold,
                    ) },
                actions = {
                    Text(
                        text = "Volumen: ${(currentVolume * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    IconButton(onClick = { playbackView.toggleAccelerometer() }) {
                        Icon(
                            if (isAccelerometerOn) Icons.Default.Sensors else Icons.Default.SensorsOff,
                            contentDescription = "Control por Movimiento",
                            tint = if (isAccelerometerOn) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (audioList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No existen grabaciones de audio en este dispositivo.", color = Color.Red)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(audioList) { item ->
                    AudioCard(
                        item = item,
                        isPlaying = isPlaying && currentlyPlayingUri == item.uri,
                        onPlayClick = {
                            if (currentlyPlayingUri == item.uri) {
                                playbackView.togglePlayPause()
                            } else {

                                playbackView.playMedia(item.uri)
                                currentlyPlayingUri = item.uri
                            }
                        }
                    )
                }
            }
        }
    }
}