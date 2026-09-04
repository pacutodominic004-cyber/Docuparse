package com.example

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.DocumentParserViewModel
import com.example.ui.components.ApiKeyDialog
import com.example.ui.screens.BenchmarkPresetsScreen
import com.example.ui.screens.DocumentCameraCaptureScreen
import com.example.ui.screens.ParserEngineScreen
import com.example.ui.screens.RecordsArchiveScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: DocumentParserViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Firebase
        com.google.firebase.FirebaseApp.initializeApp(this)
        
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val historicalDocs by viewModel.historicalDocuments.collectAsStateWithLifecycle()
                val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
                val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

                var showApiKeyDialog by remember { mutableStateOf(false) }
                var showCameraScreen by remember { mutableStateOf(false) }

                val mainPhotoPickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickVisualMedia(),
                    onResult = { uri ->
                        uri?.let { viewModel.onImageSelected(it) }
                    }
                )

                if (showCameraScreen) {
                    DocumentCameraCaptureScreen(
                        onPhotoCaptured = { bitmap, uri ->
                            showCameraScreen = false
                            viewModel.onDocumentCaptured(bitmap, uri)
                        },
                        onClose = {
                            showCameraScreen = false
                        },
                        onPickFromGallery = {
                            showCameraScreen = false
                            mainPhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    )
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = MaterialTheme.colorScheme.background,
                        topBar = {
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .drawBehind {
                                        val strokeWidth = 1.dp.toPx()
                                        drawLine(
                                            color = Color(0xFFE2E8F0),
                                            start = androidx.compose.ui.geometry.Offset(0f, size.height),
                                            end = androidx.compose.ui.geometry.Offset(size.width, size.height),
                                            strokeWidth = strokeWidth
                                        )
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .statusBarsPadding()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Brand & Identity Section
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        // Indigo scanner icon container
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Color(0xFF4F46E5)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.QrCodeScanner,
                                                contentDescription = "EastScan Engine",
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        Column {
                                            Text(
                                                text = "EastScan Engine",
                                                style = MaterialTheme.typography.titleSmall.copy(
                                                    fontSize = 13.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    letterSpacing = (-0.2).sp
                                                ),
                                                color = Color(0xFF1E293B)
                                            )
                                            Text(
                                                text = "Precision Document Parser",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Normal,
                                                    letterSpacing = 0.sp
                                                ),
                                                color = Color(0xFF64748B)
                                            )
                                        }
                                    }

                                    // Status Indicator, Quick Camera & Key Settings
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Quick Launch CameraX button
                                        IconButton(
                                            onClick = { showCameraScreen = true },
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFEEF2FF))
                                                .testTag("top_bar_camera_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CameraAlt,
                                                contentDescription = "Scan Document with Camera",
                                                tint = Color(0xFF4F46E5),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = { showApiKeyDialog = true },
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFF1F5F9))
                                                .testTag("api_key_settings_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Key,
                                                contentDescription = "API Key Settings",
                                                tint = Color(0xFF4F46E5),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        },
                    bottomBar = {
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .drawBehind {
                                    val strokeWidth = 1.dp.toPx()
                                    drawLine(
                                        color = Color(0xFFE2E8F0),
                                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                        end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                                        strokeWidth = strokeWidth
                                    )
                                }
                        ) {
                            NavigationBar(
                                containerColor = Color.Transparent,
                                tonalElevation = 0.dp
                            ) {
                                NavigationBarItem(
                                    selected = uiState.activeTab == 0,
                                    onClick = { viewModel.setActiveTab(0) },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = "Parser Engine"
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = "PARSER",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp)
                                        )
                                    },
                                    colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.White,
                                        selectedTextColor = Color(0xFF0F172A),
                                        indicatorColor = Color(0xFF0F172A),
                                        unselectedIconColor = Color(0xFF94A3B8),
                                        unselectedTextColor = Color(0xFF94A3B8)
                                    ),
                                    modifier = Modifier.testTag("nav_tab_parser")
                                )

                                NavigationBarItem(
                                    selected = uiState.activeTab == 1,
                                    onClick = { viewModel.setActiveTab(1) },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.History,
                                            contentDescription = "Records Archive"
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = "ARCHIVE (${historicalDocs.size})",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp)
                                        )
                                    },
                                    colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.White,
                                        selectedTextColor = Color(0xFF0F172A),
                                        indicatorColor = Color(0xFF0F172A),
                                        unselectedIconColor = Color(0xFF94A3B8),
                                        unselectedTextColor = Color(0xFF94A3B8)
                                    ),
                                    modifier = Modifier.testTag("nav_tab_archive")
                                )

                                NavigationBarItem(
                                    selected = uiState.activeTab == 2,
                                    onClick = { viewModel.setActiveTab(2) },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Dataset,
                                            contentDescription = "Presets Suite"
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = "BENCHMARKS",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp)
                                        )
                                    },
                                    colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.White,
                                        selectedTextColor = Color(0xFF0F172A),
                                        indicatorColor = Color(0xFF0F172A),
                                        unselectedIconColor = Color(0xFF94A3B8),
                                        unselectedTextColor = Color(0xFF94A3B8)
                                    ),
                                    modifier = Modifier.testTag("nav_tab_benchmarks")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (uiState.activeTab) {
                            0 -> ParserEngineScreen(
                                uiState = uiState,
                                onPresetSelected = { preset -> viewModel.loadPreset(preset, autoParse = true) },
                                onImageSelected = { uri -> viewModel.onImageSelected(uri) },
                                onLaunchCamera = { showCameraScreen = true },
                                onReparseRequested = { viewModel.parseCurrentDocument() },
                                onNavigateToArchive = { viewModel.setActiveTab(1) }
                            )

                            1 -> RecordsArchiveScreen(
                                documents = historicalDocs,
                                searchQuery = searchQuery,
                                selectedCategory = selectedCategory,
                                onSearchQueryChanged = { viewModel.setSearchQuery(it) },
                                onCategorySelected = { viewModel.setSelectedCategory(it) },
                                onDocumentClicked = { entity -> viewModel.viewHistoricalDocument(entity) },
                                onDeleteDocument = { id -> viewModel.deleteDocument(id) },
                                onClearAllHistory = { viewModel.clearAllHistory() }
                            )

                            2 -> BenchmarkPresetsScreen(
                                onLoadPreset = { preset ->
                                    viewModel.loadPreset(preset, autoParse = true)
                                    viewModel.setActiveTab(0)
                                }
                            )
                        }
                    }

                    if (showApiKeyDialog) {
                        ApiKeyDialog(
                            currentCustomKey = uiState.customApiKey,
                            onKeySaved = { key -> viewModel.setCustomApiKey(key) },
                            onDismiss = { showApiKeyDialog = false }
                        )
                    }
                }
            }
        }
    }
}
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
