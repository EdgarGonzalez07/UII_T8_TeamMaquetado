package utez.edu.mx.data.dao

import kotlinx.coroutines.flow.Flow
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import utez.edu.mx.data.MediaItem
import utez.edu.mx.data.Media


@Dao
interface MediaDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(item: MediaItem)

    @Query("SELECT * FROM mediaItem WHERE type = :type ORDER BY date DESC")
        fun getMediaByType(type: Media): Flow<List<MediaItem>>

}