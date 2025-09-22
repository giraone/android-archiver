package com.giraone.archiver.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.giraone.archiver.R
import com.giraone.archiver.data.FontSize
import com.giraone.archiver.data.FileRepository
import com.giraone.archiver.data.PreferencesManager
import com.giraone.archiver.data.SortOrder
import com.giraone.archiver.ui.theme.ArchiverTheme
import com.giraone.archiver.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
class PreferenceActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferencesManager = PreferencesManager(this)
        val fileRepository = FileRepository(this, preferencesManager)
        val viewModelFactory = MainViewModel.Factory(fileRepository, preferencesManager, this)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        setContent {
            ArchiverTheme {
                PreferenceScreen(
                    viewModel = viewModel,
                    onBackPressed = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceScreen(
    viewModel: MainViewModel,
    onBackPressed: () -> Unit
) {
    val sortOrder by viewModel.sortOrder.collectAsStateWithLifecycle(initialValue = SortOrder.DATE)
    val fontSize by viewModel.fontSize.collectAsStateWithLifecycle(initialValue = FontSize.NORMAL)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Sort Order Section
            Text(
                text = stringResource(R.string.sort_order),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = sortOrder == SortOrder.DATE,
                                onClick = { viewModel.setSortOrder(SortOrder.DATE) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = sortOrder == SortOrder.DATE,
                            onClick = null
                        )
                        Text(
                            text = stringResource(R.string.sort_by_date),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = sortOrder == SortOrder.FILENAME,
                                onClick = { viewModel.setSortOrder(SortOrder.FILENAME) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = sortOrder == SortOrder.FILENAME,
                            onClick = null
                        )
                        Text(
                            text = stringResource(R.string.sort_by_filename),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            // Font Size Section
            Text(
                text = stringResource(R.string.font_size),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = fontSize == FontSize.SMALL,
                                onClick = { viewModel.setFontSize(FontSize.SMALL) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = fontSize == FontSize.SMALL,
                            onClick = null
                        )
                        Text(
                            text = stringResource(R.string.font_size_small),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = fontSize == FontSize.NORMAL,
                                onClick = { viewModel.setFontSize(FontSize.NORMAL) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = fontSize == FontSize.NORMAL,
                            onClick = null
                        )
                        Text(
                            text = stringResource(R.string.font_size_normal),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = fontSize == FontSize.LARGE,
                                onClick = { viewModel.setFontSize(FontSize.LARGE) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = fontSize == FontSize.LARGE,
                            onClick = null
                        )
                        Text(
                            text = stringResource(R.string.font_size_large),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreferenceScreenPreview() {
    ArchiverTheme {
        PreferenceScreenContent(
            sortOrder = SortOrder.DATE,
            fontSize = FontSize.NORMAL,
            onSortOrderChanged = { },
            onFontSizeChanged = { },
            onBackPressed = { }
        )
    }
}

@Preview
@Composable
fun PreferenceScreenLargeFontPreview() {
    ArchiverTheme {
        PreferenceScreenContent(
            sortOrder = SortOrder.FILENAME,
            fontSize = FontSize.LARGE,
            onSortOrderChanged = { },
            onFontSizeChanged = { },
            onBackPressed = { }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceScreenContent(
    sortOrder: SortOrder,
    fontSize: FontSize,
    onSortOrderChanged: (SortOrder) -> Unit,
    onFontSizeChanged: (FontSize) -> Unit,
    onBackPressed: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Sort Order Section
            Text(
                text = stringResource(R.string.sort_order),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = sortOrder == SortOrder.DATE,
                                onClick = { onSortOrderChanged(SortOrder.DATE) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = sortOrder == SortOrder.DATE,
                            onClick = null
                        )
                        Text(
                            text = stringResource(R.string.sort_by_date),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = sortOrder == SortOrder.FILENAME,
                                onClick = { onSortOrderChanged(SortOrder.FILENAME) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = sortOrder == SortOrder.FILENAME,
                            onClick = null
                        )
                        Text(
                            text = stringResource(R.string.sort_by_filename),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            // Font Size Section
            Text(
                text = stringResource(R.string.font_size),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = fontSize == FontSize.SMALL,
                                onClick = { onFontSizeChanged(FontSize.SMALL) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = fontSize == FontSize.SMALL,
                            onClick = null
                        )
                        Text(
                            text = stringResource(R.string.font_size_small),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = fontSize == FontSize.NORMAL,
                                onClick = { onFontSizeChanged(FontSize.NORMAL) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = fontSize == FontSize.NORMAL,
                            onClick = null
                        )
                        Text(
                            text = stringResource(R.string.font_size_normal),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = fontSize == FontSize.LARGE,
                                onClick = { onFontSizeChanged(FontSize.LARGE) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = fontSize == FontSize.LARGE,
                            onClick = null
                        )
                        Text(
                            text = stringResource(R.string.font_size_large),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}