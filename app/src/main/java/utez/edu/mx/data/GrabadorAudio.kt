package utez.edu.mx.data

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.io.IOException

class GrabadorAudio (private val contexto: Context){

    private var grabador: MediaRecorder? = null

    private fun createRecorder(): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(contexto)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
    }

    fun start(outputFile: File){
        stop()

        createRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile.absolutePath)

            try{
                prepare()
                start()
                grabador = this
            } catch (e: IOException){
                e.printStackTrace()
                release()
                grabador = null
            }

        }
    }

    fun stop() {
        try {
            grabador?.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            grabador?.release()
            grabador = null
        }
    }
}