package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DocumentCategory
import com.example.data.model.ParsedDocumentEntity
import com.example.util.AccountingExportManager

@Composable
fun RecordsArchiveScreen(
    documents: List<ParsedDocumentEntity>,
    searchQuery: String,
    selectedCategory: DocumentCategory,
    onSearchQueryChanged: (String) -> Unit,
    onCategorySelected: (DocumentCategory) -> Unit,
    onDocumentClicked: (ParsedDocumentEntity) -> Unit,
    onDeleteDocument: (Long) -> Unit,
    onClearAllHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("records_archive_screen")
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Search text field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_records_input"),
            placeholder = {
                Text(
                    "Search by organization, ref, or type...",
                    fontSize = 12.5.sp,
                    color = Color(0xFF94A3B8)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color(0xFF4F46E5),
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { onSearchQueryChanged("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color(0xFF4F46E5),
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedTextColor = Color(0xFF0F172A),
                unfocusedTextColor = Color(0xFF0F172A)
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(DocumentCategory.values()) { category ->
                val isSelected = selectedCategory == category
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategorySelected(category) },
                    label = {
                        Text(
                            category.label,
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFEEF2FF),
                        selectedLabelColor = Color(0xFF4338CA),
                        containerColor = Color.White,
                        labelColor = Color(0xFF64748B)
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) Color(0xFF4F46E5) else Color(0xFFE2E8F0)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Header info row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${documents.size} SAVED RECORDS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.0.sp
                ),
                color = Color(0xFF94A3B8)
            )

            if (documents.isNotEmpty()) {
                TextButton(onClick = { showClearConfirmDialog = true }) {
                    Text(
                        "Clear All",
                        color = Color(0xFFDC2626),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Document List or Empty State
        if (documents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 96.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp),
                                tint = Color(0xFF94A3B8)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No Parsed Documents Found",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (searchQuery.isNotBlank()) "No records match '$searchQuery'" else "Documents analyzed in the engine will be preserved here.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        color = Color(0xFF64748B)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 96.dp)
            ) {
                items(documents, key = { it.id }) { doc ->
                    DocumentHistoryCard(
                        document = doc,
                        onCardClicked = { onDocumentClicked(doc) },
                        onDeleteClicked = { onDeleteDocument(doc.id) }
                    )
                }
            }
        }
    }

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("Clear All Document Records?") },
            text = { Text("This will permanently delete all parsed document history stored locally on this device.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearAllHistory()
                        showClearConfirmDialog = false
                    }
                ) {
                    Text("Clear All", color = Color(0xFFDC2626))
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Cancel", color = Color(0xFF64748B))
                }
            }
        )
    }
}

@Composable
fun DocumentHistoryCard(
    document: ParsedDocumentEntity,
    onCardClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClicked() }
            .testTag("document_history_card_${document.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFEEF2FF),
                    border = BorderStroke(1.dp, Color(0xFFE0E7FF))
                ) {
                    Text(
                        text = document.detectedType.replace("_", " "),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color(0xFF4338CA),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val isHighConf = document.confidenceScore.equals("high", ignoreCase = true)
                    val confColor = if (isHighConf) Color(0xFF059669) else Color(0xFFD97706)
                    val confBg = if (isHighConf) Color(0xFFECFDF5) else Color(0xFFFFFBEB)
                    
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = confBg
                    ) {
                        Text(
                            text = "${document.confidenceScore.uppercase()} CONF",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = confColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onDeleteClicked,
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Record",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = document.organizationName ?: "Unknown Entity",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(3.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Ref: ${document.referenceNumber ?: "N/A"}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    ),
                    color = Color(0xFF64748B)
                )
                Text(
                    text = document.primaryDate,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = Color(0xFF94A3B8)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tags & Export row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tags
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Text(
                            text = "${document.keyValuesCount} Fields",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = Color(0xFF475569),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (document.tabularRowCount > 0) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Text(
                                text = "${document.tabularRowCount} Rows",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = Color(0xFF475569),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (document.anomalyCount > 0) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFFFBEB),
                            border = BorderStroke(1.dp, Color(0xFFFEF3C7))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFD97706),
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${document.anomalyCount}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = Color(0xFF92400E)
                                )
                            }
                        }
                    }
                }

                // Direct Accounting & Document Export Buttons (DOCX, XLSX, PDF, CSV)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // DOCX (Word)
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFEFF6FF),
                        border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                        modifier = Modifier
                            .clickable {
                                AccountingExportManager.exportAndShareWordFromEntity(context, document)
                            }
                            .testTag("archive_export_word_${document.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Article,
                                contentDescription = null,
                                tint = Color(0xFF1D4ED8),
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "DOCX",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color(0xFF1E40AF)
                            )
                        }
                    }

                    // XLSX (Excel)
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF0FDF4),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                        modifier = Modifier
                            .clickable {
                                AccountingExportManager.exportAndShareExcelFromEntity(context, document)
                            }
                            .testTag("archive_export_excel_${document.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.TableChart,
                                contentDescription = null,
                                tint = Color(0xFF15803D),
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "XLSX",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color(0xFF15803D)
                            )
                        }
                    }

                    // Searchable PDF
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFFEF2F2),
                        border = BorderStroke(1.dp, Color(0xFFFECACA)),
                        modifier = Modifier
                            .clickable {
                                AccountingExportManager.exportAndSharePdfFromEntity(context, document)
                            }
                            .testTag("archive_export_pdf_${document.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "PDF",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color(0xFF991B1B)
                            )
                        }
                    }

                    // CSV
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .clickable {
                                AccountingExportManager.exportAndShareCsvFromEntity(context, document)
                            }
                            .testTag("archive_export_csv_${document.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "CSV",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color(0xFF334155)
                            )
                        }
                    }
                }
            }
        }
    }
}

