package utez.edu.mx.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import utez.edu.mx.data.AppDatabase
import utez.edu.mx.data.repositorio.RepositorioMedia
import kotlin.jvm.java

class MediaViewFactory (private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Verifica si la clase que se pide es MediaViewModel
        if (modelClass.isAssignableFrom(MediaView::class.java)) {
            // 1. Obtiene la Base de Datos
            val database = AppDatabase.getDatabase(application)
            // 2. Crea el Repositorio usando el DAO
            val repository = RepositorioMedia(database.mediaDao())

            @Suppress("UNCHECKED_CAST")
            // 3. Devuelve el ViewModel con el repositorio dentro
            return MediaView(application, repository) as T
        }
        throw IllegalArgumentException("Clase del viewmodel desconocida")
    }

}