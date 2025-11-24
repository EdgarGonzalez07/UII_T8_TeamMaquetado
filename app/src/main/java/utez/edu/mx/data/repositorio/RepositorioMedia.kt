package utez.edu.mx.data.repositorio

import kotlinx.coroutines.flow.Flow
import utez.edu.mx.data.Media
import utez.edu.mx.data.MediaItem
import utez.edu.mx.data.dao.MediaDAO


class RepositorioMedia (private val dao: MediaDAO){

    fun getAllAudios(): Flow<List<MediaItem>> {
        return dao.getMediaByType(Media.AUDIO)
    }

    fun getAllImages(): Flow<List<MediaItem>> {
        return dao.getMediaByType(Media.IMAGE)
    }

    fun getallVideos(): Flow<List<MediaItem>> {
        return dao.getMediaByType(Media.VIDEO)
    }

    suspend fun insertMedia(item: MediaItem){
        dao.insertMedia(item)
    }
}