package utez.edu.mx.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import utez.edu.mx.data.repositorio.RepoConfiguraciones
import kotlin.jvm.java

class PlaybackFactory (private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Verifica si la clase que se pide es PlaybackViewModel
        if (modelClass.isAssignableFrom(PlaybackView::class.java)) {
            // 1. Crea el repositorio de configuración (Volumen)
            val repository = RepoConfiguraciones(application)

            @Suppress("UNCHECKED_CAST")
            // 2. Devuelve el ViewModel con el repositorio dentro
            return PlaybackView(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}