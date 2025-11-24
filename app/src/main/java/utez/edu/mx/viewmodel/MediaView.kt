package utez.edu.mx.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.media.MediaMetadataRetriever

import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import utez.edu.mx.data.Media
import utez.edu.mx.data.MediaItem
import utez.edu.mx.data.repositorio.RepositorioMedia
import java.io.File

class MediaView (application: Application, private val repository: RepositorioMedia) : AndroidViewModel(application){

    val allAudio: StateFlow<List<MediaItem>> = repository.getAllAudios()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allImage: StateFlow<List<MediaItem>> = repository.getAllImages().
        stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allVideo: StateFlow<List<MediaItem>> = repository.getallVideos().
        stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun getMetadataFromUri(contentResolver: ContentResolver, uri: Uri): Pair<String, Long> {
        var aName = "Desconocido"
        var aDuration = 0L

        //Nombre
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    aName = cursor.getString(nameIndex)
                }
            }
        }

        //Duración
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(getApplication(), uri)
            val durationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            aDuration = durationString?.toLongOrNull() ?: 0L
            retriever.release()
        } catch (e: Exception) {
            aDuration = 0L
        }
        return Pair(aName, aDuration)
    }

    fun insertMediaFromUri(uri: Uri, type: Media) {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>().applicationContext
                val metadata = getMetadataFromUri(context.contentResolver, uri)
                val item = MediaItem(
                    uri = uri.toString(),
                    name = metadata.first,
                    date = System.currentTimeMillis(),
                    duration = metadata.second,
                    type = type
                )
                repository.insertMedia(item)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun insertMediaFromFile(file: File, type: Media) {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>().applicationContext
                val authority = "${context.packageName}.fileprovider"
                val uri = FileProvider.getUriForFile(context, authority, file)
                insertMediaFromUri(uri, type)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}