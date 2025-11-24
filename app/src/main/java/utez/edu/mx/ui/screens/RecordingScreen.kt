package utez.edu.mx.ui.screens

//La pantalla principal con botones grandes para grabar audio tomar fotos y videos
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import utez.edu.mx.data.GrabadorAudio
import utez.edu.mx.data.Media
import utez.edu.mx.viewmodel.MediaView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.apply
import kotlin.collections.all
import kotlin.collections.toTypedArray
import kotlin.let

@Composable
fun RecordingScreen(mediaViewModel: MediaView) {
    val context = LocalContext.current
    val audioRecorder = remember { GrabadorAudio(context) }

    var hasPermissions by remember { mutableStateOf(false) }
    var isRecordingAudio by remember { mutableStateOf(false) }
    var currentAudioFile by remember { mutableStateOf<File?>(null) }

    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    var tempVideoUri by remember { mutableStateOf<Uri?>(null) }

    val requiredPermissions = remember {
        mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasPermissions = permissions.values.all { it }
    }

    LaunchedEffect(Unit) {
        val allGranted = requiredPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

        hasPermissions = allGranted
        if (!allGranted) {
            permissionLauncher.launch(requiredPermissions)
        }
    }



    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempImageUri?.let { uri ->
                mediaViewModel.insertMediaFromUri(uri, Media.IMAGE)
            }
        }
        tempImageUri = null
    }

    // VIDEO
    val videoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo()
    ) { success ->
        if (success) {
            tempVideoUri?.let { uri ->
                mediaViewModel.insertMediaFromUri(uri, Media.VIDEO)
            }
        }
        tempVideoUri = null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        if(!hasPermissions){
            try {

            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {

            ActionButton(
                text = if (isRecordingAudio) "Detener la Grabación" else "Grabar un Audio",
                icon = if (isRecordingAudio) Icons.Default.Stop else Icons.Default.Mic,
                color = if (isRecordingAudio) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primaryContainer,
                textColor = if (isRecordingAudio) Color.White else MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.fillMaxWidth().height(120.dp),
                onClick = {
                    if (isRecordingAudio) {

                        audioRecorder.stop()
                        currentAudioFile?.let {
                            mediaViewModel.insertMediaFromFile(it, Media.AUDIO)
                        }
                        isRecordingAudio = false
                        currentAudioFile = null
                    } else {

                        val file = createTempFile(context, Media.AUDIO)
                        currentAudioFile = file
                        audioRecorder.start(file)
                        isRecordingAudio = true
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // FOTO Y VIDEO
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ActionButton(
                    text = "Foto",
                    icon = Icons.Default.CameraAlt,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    textColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.weight(1f).height(120.dp),
                    isEnabled = !isRecordingAudio,
                    onClick = {
                        val uri = createTempUri(context, Media.IMAGE)
                        tempImageUri = uri
                        imageLauncher.launch(uri)
                    }
                )
                ActionButton(
                    text = "Video",
                    icon = Icons.Default.Videocam,
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    textColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.weight(1f).height(120.dp),
                    isEnabled = !isRecordingAudio,
                    onClick = {
                        val uri = createTempUri(context, Media.VIDEO)
                        tempVideoUri = uri
                        videoLauncher.launch(uri)
                    }
                )
            }
        }
    }
}

// Componente visual personalizado para los botones cuadrados
@Composable
fun ActionButton(
    text: String,
    icon: ImageVector,
    color: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = isEnabled,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        modifier = modifier
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = textColor, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(15.dp))
            Text(text, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = textColor)
        }
    }
}

// --- HELPERS PARA CREAR ARCHIVOS Y URIS ---
private fun createTempFile(context: Context, mediaType: Media): File {
    val (dir, extension) = when (mediaType) {
        Media.AUDIO -> Pair(Environment.DIRECTORY_MUSIC, ".mp3")
        Media.IMAGE -> Pair(Environment.DIRECTORY_PICTURES, ".jpg")
        Media.VIDEO -> Pair(Environment.DIRECTORY_MOVIES, ".mp4")
    }
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val fileName = "${mediaType.name}_${timestamp}$extension"
    val storageDir = context.getExternalFilesDir(dir)
    return File(storageDir, fileName)
}

private fun createTempUri(context: Context, mediaType: Media): Uri {
    val file = createTempFile(context, mediaType)
    val authority = "${context.packageName}.fileprovider" // Debe coincidir con AndroidManifest
    return FileProvider.getUriForFile(context, authority, file)
}