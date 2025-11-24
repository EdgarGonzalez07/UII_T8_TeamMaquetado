package utez.edu.mx.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Media { AUDIO, IMAGE, VIDEO }

@Entity (tableName = "mediaItem")
data class MediaItem (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uri: String,
    val name: String,
    val date: Long,
    val duration: Long,
    val type: Media
)
