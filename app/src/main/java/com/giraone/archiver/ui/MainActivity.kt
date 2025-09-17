package com.giraone.archiver.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.giraone.archiver.R
import com.giraone.archiver.data.FileRepository
import com.giraone.archiver.data.PreferencesManager
import com.giraone.archiver.ui.components.FileListItem
import com.giraone.archiver.ui.theme.ArchiverTheme
import com.giraone.archiver.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferencesManager = PreferencesManager(this)
        val fileRepository = FileRepository(this, preferencesManager)
        val viewModelFactory = MainViewModel.Factory(fileRepository, preferencesManager, this)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        handleIntent(intent)

        setContent {
            ArchiverTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_SEND || intent.action == Intent.ACTION_SEND_MULTIPLE) {
            viewModel.handleSharedIntent(intent)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val fileItems by viewModel.fileItems.collectAsStateWithLifecycle(initialValue = emptyList())
    val fontSize by viewModel.fontSize.collectAsStateWithLifecycle(initialValue = com.giraone.archiver.data.FontSize.NORMAL)

    var showDeleteDialog by remember { mutableStateOf<com.giraone.archiver.data.FileItem?>(null) }
    var showRenameDialog by remember { mutableStateOf<com.giraone.archiver.data.FileItem?>(null) }
    var renameText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(
                        onClick = {
                            context.startActivity(Intent(context, PreferenceActivity::class.java))
                        }
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings))
                    }
                    IconButton(
                        onClick = {
                            context.startActivity(Intent(context, HelpActivity::class.java))
                        }
                    ) {
                        Icon(Icons.Default.Info, contentDescription = stringResource(R.string.help))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (fileItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_files),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(fileItems) { fileItem ->
                        FileListItem(
                            fileItem = fileItem,
                            fontSize = fontSize,
                            onDelete = { showDeleteDialog = fileItem },
                            onRename = {
                                showRenameDialog = fileItem
                                renameText = fileItem.fileName
                            },
                            onDisplay = {
                                val intent = Intent(context, FileDisplayActivity::class.java).apply {
                                    putExtra("file_id", fileItem.id)
                                }
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }

            uiState.error?.let { error ->
                LaunchedEffect(error) {
                    // Show snackbar or handle error display
                }
            }
        }
    }

    // Delete confirmation dialog
    showDeleteDialog?.let { fileItem ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text(stringResource(R.string.delete)) },
            text = { Text(stringResource(R.string.delete_confirmation)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteFile(fileItem)
                        showDeleteDialog = null
                    }
                ) {
                    Text(stringResource(R.string.yes))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = null }
                ) {
                    Text(stringResource(R.string.no))
                }
            }
        )
    }

    // Rename dialog
    showRenameDialog?.let { fileItem ->
        AlertDialog(
            onDismissRequest = { showRenameDialog = null },
            title = { Text(stringResource(R.string.rename_file)) },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    label = { Text(stringResource(R.string.new_filename)) },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (renameText.isNotBlank()) {
                            viewModel.renameFile(fileItem, renameText)
                        }
                        showRenameDialog = null
                    }
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showRenameDialog = null }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}