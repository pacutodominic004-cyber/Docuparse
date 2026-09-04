package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DocumentMetadata
import com.example.data.model.HandwritingOcrAudit
import com.example.data.model.ParsedDocumentResult
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess
import com.example.util.AccountingExportManager
import com.example.util.UniversalFormatConverter
import com.example.ui.theme.StatusWarning

@Composable
fun MetadataHeaderCard(
    metadata: DocumentMetadata,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("metadata_header_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Title & Confidence Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DOCUMENT METADATA",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.0.sp
                    ),
                    color = Color(0xFF94A3B8)
                )

                // High Confidence indigo badge matching HTML
                val (confBg, confText) = when (metadata.confidenceScore.lowercase()) {
                    "high" -> Color(0xFF4F46E5) to Color.White
                    "low" -> Color(0xFFEF4444) to Color.White
                    else -> Color(0xFFD97706) to Color.White
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = confBg
                ) {
                    Text(
                        text = "${metadata.confidenceScore.uppercase()} CONFIDENCE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = confText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2-Column Minimalist Grid matching HTML
            Row(modifier = Modifier.fillMaxWidth()) {
                // Column 1
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cell 1: Detected Type
                    Column {
                        Text(
                            text = "DETECTED TYPE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.6.sp
                            ),
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = formatDetectedType(metadata.detectedType),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color(0xFF1E293B),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Cell 3: Organization
                    Column {
                        Text(
                            text = "ORGANIZATION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.6.sp
                            ),
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = metadata.organizationName ?: "N/A",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color(0xFF1E293B),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Column 2
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cell 2: Primary Date
                    Column {
                        Text(
                            text = "PRIMARY DATE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.6.sp
                            ),
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (metadata.primaryDate.isNotBlank()) metadata.primaryDate else "N/A",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color(0xFF1E293B),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Cell 4: Reference
                    Column {
                        Text(
                            text = "REFERENCE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.6.sp
                            ),
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = metadata.referenceNumber ?: "N/A",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color(0xFF1E293B),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            metadata.referenceNumber?.let { ref ->
                                IconButton(
                                    onClick = { copyToClipboard(context, "Reference", ref) },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Reference",
                                        tint = Color(0xFF4F46E5),
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KeyValuesSection(
    keyValues: Map<String, String>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "EXTRACTED RECORDS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.0.sp
                    ),
                    color = Color(0xFF94A3B8)
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFEEF2FF)
                ) {
                    Text(
                        text = "${keyValues.size} FIELDS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color(0xFF4338CA),
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Clean Minimalist Key-Value Rows matching HTML design
            Column {
                val entries = keyValues.entries.toList()
                entries.forEachIndexed { index, (key, value) ->
                    val isCurrency = value.contains("UGX") || value.contains("KES") || value.contains("TZS") || value.contains("RWF")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { copyToClipboard(context, key, value) }
                            .padding(vertical = 9.dp, horizontal = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatKeyLabel(key),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            color = Color(0xFF475569),
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = value,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (isCurrency) Color(0xFF059669) else Color(0xFF0F172A)
                        )
                    }

                    if (index < entries.size - 1) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0xFFF1F5F9))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TabularDataSection(
    tableData: List<Map<String, String>>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    if (tableData.isEmpty()) return

    val columns = tableData.firstOrNull()?.keys?.toList() ?: emptyList()

    Card(
        modifier = modifier.fillMaxWidth(),
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
                        text = "RECONSTRUCTED TABULAR DATA",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.0.sp
                        ),
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${tableData.size} rows transcribed from document grid",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = Color(0xFF64748B)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF1F5F9),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.clickable {
                            val csv = convertTableToCsv(tableData)
                            copyToClipboard(context, "Table CSV", csv)
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy CSV",
                                tint = Color(0xFF475569),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "Copy CSV",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color(0xFF334155)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFEEF2FF),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E7FF)),
                        modifier = Modifier.clickable {
                            shareText(context, convertTableToCsv(tableData), "Reconstructed Table CSV")
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Table CSV",
                                tint = Color(0xFF4F46E5),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "Share CSV",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color(0xFF4338CA)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Scrollable Grid Table
            val horizontalScrollState = rememberScrollState()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                    .horizontalScroll(horizontalScrollState)
            ) {
                Column {
                    // Header row
                    Row(
                        modifier = Modifier
                            .background(Color(0xFFF8FAFC))
                            .padding(vertical = 9.dp)
                    ) {
                        columns.forEach { col ->
                            Text(
                                text = formatKeyLabel(col).uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = Color(0xFF475569),
                                modifier = Modifier
                                    .width(160.dp)
                                    .padding(horizontal = 12.dp)
                            )
                        }
                    }

                    // Data rows
                    tableData.forEachIndexed { index, row ->
                        val bg = if (index % 2 == 0) Color.White else Color(0xFFF8FAFC)
                        Row(
                            modifier = Modifier
                                .background(bg)
                                .padding(vertical = 8.dp)
                        ) {
                            columns.forEach { col ->
                                Text(
                                    text = row[col] ?: "-",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.5.sp,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = Color(0xFF0F172A),
                                    modifier = Modifier
                                        .width(160.dp)
                                        .padding(horizontal = 12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FlaggedAnomaliesSection(
    anomalies: List<String>,
    modifier: Modifier = Modifier
) {
    if (anomalies.isEmpty()) return

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFEF3C7)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFD97706),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "FLAGGED ANOMALIES (${anomalies.size})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    ),
                    color = Color(0xFF92400E)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            anomalies.forEach { anomaly ->
                Text(
                    text = anomaly,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        fontStyle = FontStyle.Italic,
                        lineHeight = 16.sp
                    ),
                    color = Color(0xFFB45309),
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun JsonViewerSection(
    rawJson: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
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
                Text(
                    text = "STRUCTURED JSON OUTPUT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.0.sp
                    ),
                    color = Color(0xFF94A3B8)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = { copyToClipboard(context, "Parsed JSON", rawJson) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy JSON",
                            tint = Color(0xFF4F46E5),
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    IconButton(
                        onClick = { shareText(context, rawJson, "DocuParse EA JSON Export") },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share JSON",
                            tint = Color(0xFF4F46E5),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF8FAFC),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                val displayText = if (isExpanded || rawJson.length < 400) rawJson else rawJson.take(400) + "\n... (tap expand to view full JSON)"
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = displayText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        ),
                        color = Color(0xFF1E293B)
                    )
                    if (rawJson.length >= 400) {
                        OutlinedButton(
                            onClick = { isExpanded = !isExpanded },
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .align(Alignment.End),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Text(
                                if (isExpanded) "Collapse" else "Expand Full JSON",
                                fontSize = 10.sp,
                                color = Color(0xFF4F46E5)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Helpers
private fun formatDetectedType(type: String): String {
    return when (type) {
        "SACCO_Receipt" -> "SACCO & Microfinance Receipt"
        "School_Exam" -> "School Continuous Assessment"
        "Fleet_Fuel_Invoice" -> "Fleet Fuel POS & Route Log"
        "EFRIS_Tax_Invoice" -> "URA EFRIS Tax Invoice"
        else -> type.replace("_", " ")
    }
}

private fun getDocumentTypeStyle(type: String): Pair<ImageVector, Color> {
    return when {
        type.contains("SACCO", ignoreCase = true) || type.contains("Saving", ignoreCase = true) ->
            Icons.Default.AccountBalance to Color(0xFF059669)

        type.contains("School", ignoreCase = true) || type.contains("Exam", ignoreCase = true) ->
            Icons.Default.School to Color(0xFF0284C7)

        type.contains("Fuel", ignoreCase = true) || type.contains("Fleet", ignoreCase = true) ->
            Icons.Default.LocalGasStation to Color(0xFFD97706)

        else -> Icons.Default.Receipt to Color(0xFF7C3AED)
    }
}

private fun formatKeyLabel(key: String): String {
    return key.replace("_", " ")
        .split(" ")
        .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
}

private fun convertTableToCsv(tableData: List<Map<String, String>>): String {
    if (tableData.isEmpty()) return ""
    val headers = tableData.first().keys.toList()
    val builder = StringBuilder()
    builder.append(headers.joinToString(",") { "\"$it\"" }).append("\n")
    for (row in tableData) {
        builder.append(headers.joinToString(",") { "\"${row[it] ?: ""}\"" }).append("\n")
    }
    return builder.toString()
}

fun copyToClipboard(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "$label copied to clipboard", Toast.LENGTH_SHORT).show()
}

fun shareText(context: Context, text: String, title: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, title))
}

@Composable
fun HandwritingOcrAuditCard(
    audit: HandwritingOcrAudit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("handwriting_ocr_audit_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFEEF2FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = null,
                            tint = Color(0xFF4F46E5),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "HANDWRITING OCR AUDIT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.0.sp
                            ),
                            color = Color(0xFF4F46E5)
                        )
                        Text(
                            text = "East African Manual Records Pipeline",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color(0xFF0F172A)
                        )
                    }
                }

                // Accuracy Badge
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = Color(0xFFECFDF5),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0)),
                    modifier = Modifier.testTag("ocr_accuracy_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = audit.transcriptionAccuracyRating,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color(0xFF065F46)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Identified Script Pill
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF8FAFC),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Identified Script / Cursive Style",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = audit.cursiveStyleIdentified,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = Color(0xFF1E293B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4 Processing Pillars Grid
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OcrPillarChip(
                        modifier = Modifier.weight(1f),
                        title = "Cursive Script",
                        subtitle = "Marion Richardson / Palmer",
                        isActive = audit.handwritingDetected
                    )
                    OcrPillarChip(
                        modifier = Modifier.weight(1f),
                        title = "Ink Density",
                        subtitle = "Dynamic Range Stretched",
                        isActive = audit.inkDensityCompensationApplied
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OcrPillarChip(
                        modifier = Modifier.weight(1f),
                        title = "Blurred Text",
                        subtitle = "Unsharp Mask Recovered",
                        isActive = audit.blurCompensationApplied
                    )
                    OcrPillarChip(
                        modifier = Modifier.weight(1f),
                        title = "Arithmetic Proof",
                        subtitle = "100% Cross-Reconciled",
                        isActive = audit.mathematicalReconciliationVerified
                    )
                }
            }
        }
    }
}

@Composable
private fun OcrPillarChip(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    isActive: Boolean
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = if (isActive) Color(0xFFFAFAFA) else Color(0xFFF1F5F9),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isActive) Color(0xFFE2E8F0) else Color(0xFFCBD5E1)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isActive) Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = null,
                tint = if (isActive) Color(0xFF059669) else Color(0xFF94A3B8),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = Color(0xFF64748B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun AccountingExportActionsCard(
    result: ParsedDocumentResult,
    modifier: Modifier = Modifier,
    sourceBitmap: Bitmap? = null
) {
    UniversalFormatConversionCard(
        result = result,
        modifier = modifier,
        sourceBitmap = sourceBitmap
    )
}

@Composable
fun UniversalFormatConversionCard(
    result: ParsedDocumentResult,
    modifier: Modifier = Modifier,
    sourceBitmap: Bitmap? = null
) {
    val context = LocalContext.current
    var showImageFormatDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("accounting_export_actions_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFEEF2FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Article,
                            contentDescription = null,
                            tint = Color(0xFF4F46E5),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "UNIVERSAL FORMAT CONVERSION ENGINE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            ),
                            color = Color(0xFF4F46E5)
                        )
                        Text(
                            text = "One-click conversion into editable Office & audit outputs",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.5.sp,
                                color = Color(0xFF64748B)
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = Color(0xFFF1F5F9),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Text(
                        text = "5 FORMATS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF475569),
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Row 1: Word (.docx) & Excel (.xlsx)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Export Word (.docx) Card Button
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            UniversalFormatConverter.exportAndShareWordDocx(context, result)
                        }
                        .testTag("export_word_docx_button"),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFEFF6FF),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFDBEAFE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = Color(0xFF1D4ED8),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = Color(0xFF1D4ED8),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Word (.docx)",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color(0xFF1E40AF)
                        )
                        Text(
                            text = "100% Editable formatted document",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = Color(0xFF1D4ED8),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Export Excel (.xlsx) Card Button
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            UniversalFormatConverter.exportAndShareExcelXlsx(context, result)
                        }
                        .testTag("export_excel_xlsx_button"),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF0FDF4),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFDCFCE7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TableChart,
                                    contentDescription = null,
                                    tint = Color(0xFF15803D),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = Color(0xFF15803D),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Excel (.xlsx)",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color(0xFF15803D)
                        )
                        Text(
                            text = "Structured multi-column workbook",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = Color(0xFF166534),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 2: Searchable PDF (.pdf) & High-Res Images (PNG/JPG)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Export Searchable PDF Card Button
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            UniversalFormatConverter.exportAndShareSearchablePdf(context, result, sourceBitmap)
                        }
                        .testTag("export_pdf_action_button"),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFEF2F2),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFEE2E2)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = null,
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Searchable PDF",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color(0xFF991B1B)
                        )
                        Text(
                            text = "Highlightable & searchable text",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = Color(0xFFB91C1C),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Export High-Res Image Card Button
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            showImageFormatDialog = true
                        }
                        .testTag("export_image_button"),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFAF5FF),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE9D5FF))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF3E8FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    tint = Color(0xFF7E22CE),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = Color(0xFF7E22CE),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "High-Res Image",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color(0xFF6B21A8)
                        )
                        Text(
                            text = "Lossless PNG & 95% JPEG",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = Color(0xFF7E22CE),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 3: CSV Export, Copy CSV, Share JSON
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        UniversalFormatConverter.exportAndShareCsv(context, result)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("export_csv_action_button"),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Icon(
                        imageVector = Icons.Default.TableChart,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "CSV",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp, fontWeight = FontWeight.Bold),
                        color = Color(0xFF0F172A)
                    )
                }

                OutlinedButton(
                    onClick = {
                        UniversalFormatConverter.copyCsvToClipboard(context, result)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("copy_accounting_csv_button"),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = Color(0xFF475569)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Copy CSV",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                        color = Color(0xFF334155)
                    )
                }

                OutlinedButton(
                    onClick = {
                        shareText(context, result.rawJson, "DocuParse EA JSON Export")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("share_json_button"),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = Color(0xFF475569)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "JSON",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                        color = Color(0xFF334155)
                    )
                }
            }
        }
    }

    // High-Res Image Format Selection Dialog
    if (showImageFormatDialog) {
        AlertDialog(
            onDismissRequest = { showImageFormatDialog = false },
            title = {
                Text(
                    text = "Export High-Resolution Image",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    color = Color(0xFF0F172A)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Choose image encoding format for this document capture:",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = Color(0xFF64748B)
                    )

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showImageFormatDialog = false
                                UniversalFormatConverter.exportAndShareHighResImage(
                                    context = context,
                                    result = result,
                                    sourceBitmap = sourceBitmap,
                                    format = UniversalFormatConverter.ImageExportFormat.PNG
                                )
                            }
                            .testTag("export_image_png_option"),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFEDE9FE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    tint = Color(0xFF7C3AED),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Lossless PNG Image (.png)",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "100% Crisp, uncompressed pixel-perfect preservation",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showImageFormatDialog = false
                                UniversalFormatConverter.exportAndShareHighResImage(
                                    context = context,
                                    result = result,
                                    sourceBitmap = sourceBitmap,
                                    format = UniversalFormatConverter.ImageExportFormat.JPEG
                                )
                            }
                            .testTag("export_image_jpeg_option"),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFEF3C7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    tint = Color(0xFFD97706),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "High-Quality JPEG (.jpg)",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "95% Quality optimized for sharing via email/messaging",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showImageFormatDialog = false }) {
                    Text("Cancel", color = Color(0xFF64748B))
                }
            }
        )
    }
}

