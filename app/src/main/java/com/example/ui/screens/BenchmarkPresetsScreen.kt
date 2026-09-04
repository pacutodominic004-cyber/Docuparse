package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DocumentCategory
import com.example.data.model.SamplePreset
import com.example.data.model.SamplePresetsData

@Composable
fun BenchmarkPresetsScreen(
    onLoadPreset: (SamplePreset) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("benchmark_presets_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 12.dp, bottom = 96.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFEEF2FF),
                            border = BorderStroke(1.dp, Color(0xFFE0E7FF)),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color(0xFF4F46E5),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "EAST AFRICAN BENCHMARK SUITE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.0.sp
                                ),
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = "Field Document Presets",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = Color(0xFF0F172A)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "High-precision parsing templates tailored for regional business practices, currency formats (UGX, KES), hand-graded scores, and URA EFRIS compliance.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        ),
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        items(SamplePresetsData.presets) { preset ->
            PresetBenchmarkCard(
                preset = preset,
                onLoadClicked = { onLoadPreset(preset) }
            )
        }
    }
}

@Composable
fun PresetBenchmarkCard(
    preset: SamplePreset,
    onLoadClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, badgeColor, badgeBg) = when (preset.category) {
        DocumentCategory.SACCO -> Triple(Icons.Default.AccountBalance, Color(0xFF059669), Color(0xFFECFDF5))
        DocumentCategory.SCHOOL -> Triple(Icons.Default.School, Color(0xFF0284C7), Color(0xFFF0F9FF))
        DocumentCategory.FLEET_CPA -> Triple(Icons.Default.LocalGasStation, Color(0xFFD97706), Color(0xFFFFFBEB))
        else -> Triple(Icons.Default.Receipt, Color(0xFF4F46E5), Color(0xFFEEF2FF))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("benchmark_card_${preset.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeBg,
                    border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = badgeColor,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = preset.category.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = badgeColor
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Text(
                        text = preset.detectedType,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        ),
                        color = Color(0xFF475569),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = preset.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFF0F172A)
            )

            Text(
                text = preset.subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                color = Color(0xFF4F46E5),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = preset.sampleImageDescription,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp
                ),
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onLoadClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0F172A),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "LOAD & PARSE IN ENGINE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                )
            }
        }
    }
}

