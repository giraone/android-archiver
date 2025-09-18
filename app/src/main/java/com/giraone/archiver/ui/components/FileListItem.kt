package com.giraone.archiver.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow // Display Action
import androidx.compose.material.icons.filled.AccountBox // Image Content Type
import androidx.compose.material.icons.filled.Menu // Text Content Type
import androidx.compose.material.icons.filled.Info // Markdown Content Type
import androidx.compose.material.icons.filled.Warning // Other Content Type
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.giraone.archiver.data.FileItem
import com.giraone.archiver.data.FileType
import com.giraone.archiver.data.FontSize
import com.giraone.archiver.utils.FileUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileListItem(
    fileItem: FileItem,
    fontSize: FontSize,
    onDelete: () -> Unit,
    onRename: () -> Unit,
    onDisplay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    val textSize = when (fontSize) {
        FontSize.SMALL -> 12.sp
        FontSize.NORMAL -> 14.sp
        FontSize.LARGE -> 16.sp
    }

    val iconSize = when (fontSize) {
        FontSize.SMALL -> 20.dp
        FontSize.NORMAL -> 24.dp
        FontSize.LARGE -> 28.dp
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { showMenu = true }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = getFileTypeIcon(fileItem.fileType),
                contentDescription = null,
                modifier = Modifier.size(iconSize),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = fileItem.fileName,
                    fontSize = textSize,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row {
                    Text(
                        text = FileUtils.formatFileSize(context, fileItem.sizeInBytes),
                        fontSize = (textSize.value - 2).sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = FileUtils.formatDateTime(fileItem.storageDateTime),
                        fontSize = (textSize.value - 2).sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                if (fileItem.fileType == FileType.IMAGE || fileItem.fileType == FileType.TEXT || fileItem.fileType == FileType.MARKDOWN) {
                    DropdownMenuItem(
                        text = { Text("Display") },
                        onClick = {
                            showMenu = false
                            onDisplay()
                        },
                        leadingIcon = {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                        }
                    )
                }

                DropdownMenuItem(
                    text = { Text("Rename") },
                    onClick = {
                        showMenu = false
                        onRename()
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Edit, contentDescription = null)
                    }
                )

                DropdownMenuItem(
                    text = { Text("Delete") },
                    onClick = {
                        showMenu = false
                        onDelete()
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Delete, contentDescription = null)
                    }
                )
            }
        }
    }
}

private fun getFileTypeIcon(fileType: FileType): ImageVector {
    return when (fileType) {
        FileType.IMAGE -> Icons.Default.AccountBox
        FileType.TEXT -> Icons.Default.Menu
        FileType.MARKDOWN -> Icons.Default.Info
        FileType.OTHER -> Icons.Default.Warning
    }
}