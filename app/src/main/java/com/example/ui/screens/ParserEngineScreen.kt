package com.example.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import com.example.util.UniversalFormatConverter
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SamplePreset
import com.example.data.model.SamplePresetsData
import com.example.data.model.QueuedDocumentEntity
import com.example.ui.ParserUiState
import com.example.ui.components.AccountingExportActionsCard
import com.example.ui.components.FlaggedAnomaliesSection
import com.example.ui.components.HandwritingOcrAuditCard
import com.example.ui.components.JsonViewerSection
import com.example.ui.components.KeyValuesSection
import com.example.ui.components.MetadataHeaderCard
import com.example.ui.components.OfflineQueueDeck
import com.example.ui.components.TabularDataSection
import com.example.ui.components.copyToClipboard
import com.example.ui.components.shareText
import com.example.ui.theme.StatusError
import com.example.util.AccountingExportManager
import com.example.util.DocumentFilterMode
import com.example.util.NetworkStatus
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.HighQuality

@Composable
fun ParserEngineScreen(
    uiState: ParserUiState,
    networkStatus: NetworkStatus? = null,
    queuedDocuments: List<QueuedDocumentEntity> = emptyList(),
    onPresetSelected: (SamplePreset) -> Unit,
    onImageSelected: (android.net.Uri) -> Unit,
    onLaunchCamera: () -> Unit,
    onReparseRequested: () -> Unit,
    onNavigateToArchive: () -> Unit,
    onFilterSelected: ((DocumentFilterMode) -> Unit)? = null,
    onQueueOfflineClick: (() -> Unit)? = null,
    onSyncQueueClick: (() -> Unit)? = null,
    onDeleteItemClick: ((Long) -> Unit)? = null,
    onRetryItemClick: ((QueuedDocumentEntity) -> Unit)? = null,
    onClearSyncedClick: (() -> Unit)? = null,
    onDismissQueueMessage: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Zero-permission Android Photo Picker contract
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { onImageSelected(it) }
        }
    )

    var isImageZoomed by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("parser_engine_screen"),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 12.dp,
            bottom = 96.dp
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Scanning Segment Banner (matching Clean Minimalism HTML)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF1F5F9))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                    .drawBehind {
                        // laser scan line at top
                        drawLine(
                            color = Color(0xFF4F46E5),
                            start = androidx.compose.ui.geometry.Offset(0f, 0f),
                            end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                            strokeWidth = 2.dp.toPx()
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "SCANNING SEGMENT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFEEF2FF)
                        ) {
                            Text(
                                text = uiState.activePreset?.let { "${it.id.uppercase()}.JPG" } ?: "IMAGE_REF_082.JPG",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = Color(0xFF4338CA),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = if (uiState.currentResult != null) "98% OCR Match" else "Ready for Ingestion",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }

        // Document Ingestion Action Bar
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "DOCUMENT INGESTION ENGINE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.0.sp
                        ),
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("pick_document_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0F172A),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "PICK FILE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                ),
                                maxLines = 1
                            )
                        }

                        FilledTonalButton(
                            onClick = onLaunchCamera,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("capture_photo_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFFEEF2FF),
                                contentColor = Color(0xFF4F46E5)
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E7FF))
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "HD CAMERA",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // East African Quick Benchmark Presets Selector Row
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DOCUMENT PRESETS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.0.sp
                        ),
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = "${SamplePresetsData.presets.size} EA TYPES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color(0xFF4F46E5)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(SamplePresetsData.presets) { preset ->
                        val isSelected = uiState.activePreset?.id == preset.id
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFFEEF2FF) else Color.White,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) Color(0xFF4F46E5) else Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier
                                .clickable { onPresetSelected(preset) }
                                .testTag("preset_chip_${preset.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color(0xFF4F46E5),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Column {
                                    Text(
                                        text = preset.title.take(24) + if (preset.title.length > 24) "..." else "",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontSize = 11.5.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (isSelected) Color(0xFF4338CA) else Color(0xFF1E293B)
                                    )
                                    Text(
                                        text = preset.category.label,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active Document Image Viewer
        item {
            uiState.selectedBitmap?.let { bitmap ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = uiState.activePreset?.title ?: "Captured Document",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color(0xFF0F172A),
                                    maxLines = 1
                                )
                                Text(
                                    text = "${bitmap.width} × ${bitmap.height} px • ACTIVE FEED",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.5.sp,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = Color(0xFF64748B)
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = { isImageZoomed = !isImageZoomed },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isImageZoomed) Icons.Default.ZoomOut else Icons.Default.ZoomIn,
                                        contentDescription = "Toggle Zoom",
                                        tint = Color(0xFF4F46E5),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                IconButton(
                                    onClick = onReparseRequested,
                                    enabled = !uiState.isParsing,
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Reparse Document",
                                        tint = Color(0xFF4F46E5),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(if (isImageZoomed) 450.dp else 210.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                                .background(Color(0xFFF8FAFC))
                        ) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Active Document Preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = if (isImageZoomed) ContentScale.Fit else ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // On-Device AI Image Filtering (Monochrome / Magic Color / Grayscale)
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FilterAlt,
                                        contentDescription = null,
                                        tint = Color(0xFF4F46E5),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "LOCAL ON-DEVICE ENHANCEMENT",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        ),
                                        color = Color(0xFF4F46E5)
                                    )
                                }
                                Text(
                                    text = uiState.activeFilterMode.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        color = Color(0xFF64748B)
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(DocumentFilterMode.values()) { mode ->
                                    val isSelected = uiState.activeFilterMode == mode
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) Color(0xFF4F46E5) else Color(0xFFF1F5F9),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isSelected) Color(0xFF4F46E5) else Color(0xFFCBD5E1)
                                        ),
                                        modifier = Modifier.clickable { onFilterSelected?.invoke(mode) }
                                    ) {
                                        Text(
                                            text = mode.label,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) Color.White else Color(0xFF334155)
                                            ),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Compression Footprint Metrics Card (~150KB target)
                        uiState.latestCompressionStats?.let { stats ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF0FDF4),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCFCE7)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "HIGH COMPRESSION FOOTPRINT",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 0.5.sp
                                            ),
                                            color = Color(0xFF16A34A)
                                        )
                                        Text(
                                            text = "${stats.formattedOriginalSize} photo → ${stats.formattedCompressedSize} (${String.format("%.0f", stats.reductionPercentage)}% reduction)",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = Color(0xFF15803D)
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFF16A34A)
                                    ) {
                                        Text(
                                            text = "150KB Target",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Actions: Queue Offline & Export 150KB PDF
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onQueueOfflineClick?.invoke() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .testTag("queue_offline_button"),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudUpload,
                                    contentDescription = null,
                                    tint = Color(0xFF4F46E5),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Queue Offline",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF334155)
                                    )
                                )
                            }

                            Button(
                                onClick = {
                                    UniversalFormatConverter.exportAndShareUltraCompressedPdf(
                                        context = context,
                                        bitmap = bitmap,
                                        title = uiState.activePreset?.title ?: "Scanned_Document"
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .testTag("export_compressed_pdf_button"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4F46E5),
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "150KB PDF",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Queue Confirmation Toast/Card
        uiState.queueSuccessMessage?.let { msg ->
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFECFDF5),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD1FAE5)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF059669),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = Color(0xFF065F46)
                            )
                        }
                        IconButton(
                            onClick = { onDismissQueueMessage?.invoke() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = Color(0xFF059669),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // Offline Document Queue Section (when items are queued or device is offline)
        if (networkStatus != null && queuedDocuments.isNotEmpty()) {
            item {
                OfflineQueueDeck(
                    networkStatus = networkStatus,
                    queuedDocuments = queuedDocuments,
                    isSyncing = uiState.isSyncingQueue,
                    onSyncNowClick = { onSyncQueueClick?.invoke() },
                    onDeleteItemClick = { id -> onDeleteItemClick?.invoke(id) },
                    onRetryItemClick = { item -> onRetryItemClick?.invoke(item) },
                    onClearSyncedClick = { onClearSyncedClick?.invoke() }
                )
            }
        }

        // Parsing Progress Status
        if (uiState.isParsing) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF2FF)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E7FF))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = Color(0xFF4F46E5)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Parsing East African Document...",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color(0xFF4338CA)
                                )
                                Text(
                                    text = uiState.parsingStatusMessage,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFF4F46E5),
                            trackColor = Color(0xFFE0E7FF)
                        )
                    }
                }
            }
        }

        // Error message card
        uiState.errorMessage?.let { errorMsg ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Parser Notice",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color(0xFFDC2626)
                            )
                            Text(
                                text = errorMsg,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = Color(0xFF7F1D1D)
                            )
                        }
                    }
                }
            }
        }

        // Parsed Results Sections (strictly following required schema)
        uiState.currentResult?.let { result ->
            item {
                MetadataHeaderCard(metadata = result.metadata)
            }

            result.handwritingAudit?.let { audit ->
                item {
                    HandwritingOcrAuditCard(audit = audit)
                }
            }

            item {
                AccountingExportActionsCard(
                    result = result,
                    sourceBitmap = uiState.selectedBitmap
                )
            }

            if (result.extractedKeyValues.isNotEmpty()) {
                item {
                    KeyValuesSection(keyValues = result.extractedKeyValues)
                }
            }

            if (result.tabularData.isNotEmpty()) {
                item {
                    TabularDataSection(tableData = result.tabularData)
                }
            }

            if (result.flaggedAnomalies.isNotEmpty()) {
                item {
                    FlaggedAnomaliesSection(anomalies = result.flaggedAnomalies)
                }
            }

            item {
                JsonViewerSection(rawJson = result.rawJson)
            }

            // Quick Action Footer Button matching Clean Minimalism design
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            UniversalFormatConverter.exportAndShareWordDocx(context, result)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("footer_export_word_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1D4ED8),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Article,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Word",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }

                    Button(
                        onClick = {
                            UniversalFormatConverter.exportAndShareExcelXlsx(context, result)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("footer_export_excel_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF15803D),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.TableChart,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Excel",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }

                    Button(
                        onClick = {
                            UniversalFormatConverter.exportAndShareSearchablePdf(context, result, uiState.selectedBitmap)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("footer_export_pdf_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFDC2626),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "PDF",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF1F5F9),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .size(46.dp)
                            .clickable {
                                copyToClipboard(context, "Parsed JSON", result.rawJson)
                            }
                            .testTag("footer_copy_json_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = "Copy JSON",
                                tint = Color(0xFF0F172A),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

