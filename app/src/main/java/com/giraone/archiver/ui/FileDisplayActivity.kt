package com.giraone.archiver.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.giraone.archiver.R
import com.giraone.archiver.data.FileRepository
import com.giraone.archiver.data.FileType
import com.giraone.archiver.data.PreferencesManager
import com.giraone.archiver.ui.theme.ArchiverTheme
import com.giraone.archiver.viewmodel.MainViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
class FileDisplayActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferencesManager = PreferencesManager(this)
        val fileRepository = FileRepository(this, preferencesManager)
        val viewModelFactory = MainViewModel.Factory(fileRepository, preferencesManager, this)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        val fileId = intent.getStringExtra("file_id") ?: ""

        setContent {
            ArchiverTheme {
                FileDisplayScreen(
                    fileId = fileId,
                    viewModel = viewModel,
                    onBackPressed = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileDisplayScreen(
    fileId: String,
    viewModel: MainViewModel,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    var fileContent by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val fileItems by viewModel.fileItems.collectAsStateWithLifecycle(initialValue = emptyList())
    val fileItem = remember(fileId, fileItems) {
        fileItems.find { it.id == fileId }
    }

    LaunchedEffect(fileItem) {
        if (fileItem != null && fileItem.fileType == FileType.TEXT) {
            try {
                isLoading = true
                val file = File(fileItem.filePath)
                fileContent = file.readText()
                error = null
            } catch (e: Exception) {
                error = "Cannot read file: ${e.message}"
                fileContent = null
            } finally {
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(fileItem?.fileName ?: stringResource(R.string.file_display)) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (fileItem == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.error_file_not_found),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            when (fileItem.fileType) {
                FileType.IMAGE -> {
                    ImageDisplayContent(
                        filePath = fileItem.filePath,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )
                }
                FileType.TEXT -> {
                    TextDisplayContent(
                        content = fileContent,
                        isLoading = isLoading,
                        error = error,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )
                }
                FileType.OTHER -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "File type not supported for display",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ImageDisplayContent(
    filePath: String,
    modifier: Modifier = Modifier
) {
    var imageBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(filePath) {
        try {
            isLoading = true
            val bitmap = android.graphics.BitmapFactory.decodeFile(filePath)
            if (bitmap != null) {
                imageBitmap = bitmap
                error = null
            } else {
                error = "Failed to load image"
            }
        } catch (e: Exception) {
            error = "Error loading image: ${e.message}"
            imageBitmap = null
        } finally {
            isLoading = false
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> CircularProgressIndicator()
            error != null -> Text(
                text = "error", // TODO was error
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
            imageBitmap != null -> {
                Image(
                    bitmap = imageBitmap!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
            else -> Text(
                text = "No image to display",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun TextDisplayContent(
    content: String?,
    isLoading: Boolean,
    error: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = if (isLoading || error != null) Alignment.Center else Alignment.TopStart
    ) {
        when {
            isLoading -> CircularProgressIndicator()
            error != null -> Text(
                text = error,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
            content != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = content,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 20.sp
                    )
                }
            }
            else -> Text(
                text = "No content to display",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

