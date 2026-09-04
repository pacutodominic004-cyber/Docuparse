package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Collections
import com.example.util.LocalDocumentProcessor
import com.example.util.DocumentCompressor
import com.example.util.DocumentFilterMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

data class BatchScannedPage(
    val pageNumber: Int,
    val bitmap: Bitmap,
    val file: File,
    val originalSizeBytes: Long,
    val compressedSizeBytes: Long
)

@Composable
fun DocumentCameraCaptureScreen(
    onPhotoCaptured: (Bitmap, Uri?) -> Unit,
    onClose: () -> Unit,
    onPickFromGallery: () -> Unit,
    onBatchQueued: ((List<Pair<Bitmap, File>>, DocumentFilterMode) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    if (!hasCameraPermission) {
        CameraPermissionRequestView(
            onRequestPermission = {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            },
            onClose = onClose,
            onPickFromGallery = onPickFromGallery,
            modifier = modifier
        )
    } else {
        CameraXDocumentScannerView(
            onPhotoCaptured = onPhotoCaptured,
            onClose = onClose,
            onPickFromGallery = onPickFromGallery,
            onBatchQueued = onBatchQueued,
            modifier = modifier
        )
    }
}

@Composable
private fun CameraPermissionRequestView(
    onRequestPermission: () -> Unit,
    onClose: () -> Unit,
    onPickFromGallery: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .testTag("camera_permission_request_view"),
        contentAlignment = Alignment.Center
    ) {
        // Back / Close Button at top
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(42.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.12f))
                .testTag("camera_permission_close_button")
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close Camera",
                tint = Color.White
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEEF2FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = Color(0xFF4F46E5),
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Camera Access Required",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFF0F172A),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "EastScan uses the high-resolution CameraX engine to photograph and digitize SACCO ledgers, fuel receipts, invoices, and examination marksheets with sharp OCR precision.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    ),
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onRequestPermission,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("grant_camera_permission_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4F46E5),
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "GRANT CAMERA PERMISSION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onPickFromGallery,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("pick_from_gallery_fallback_button"),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        tint = Color(0xFF475569),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Pick Document from Files Instead",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = Color(0xFF334155)
                    )
                }
            }
        }
    }
}

@Composable
private fun CameraXDocumentScannerView(
    onPhotoCaptured: (Bitmap, Uri?) -> Unit,
    onClose: () -> Unit,
    onPickFromGallery: () -> Unit,
    onBatchQueued: ((List<Pair<Bitmap, File>>, DocumentFilterMode) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var camera by remember { mutableStateOf<Camera?>(null) }
    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var flashMode by remember { mutableStateOf(ImageCapture.FLASH_MODE_AUTO) }
    var isCapturing by remember { mutableStateOf(false) }
    var captureError by remember { mutableStateOf<String?>(null) }

    // Batch & Filter State
    var isBatchMode by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf(DocumentFilterMode.MONOCHROME_HIGH_CONTRAST) }
    val batchPages = remember { mutableStateListOf<BatchScannedPage>() }
    var showBatchReviewDialog by remember { mutableStateOf(false) }
    var statusToastMessage by remember { mutableStateOf<String?>(null) }

    // Tap to focus indicators
    var tapFocusPoint by remember { mutableStateOf<Offset?>(null) }

    var previewViewInstance by remember { mutableStateOf<PreviewView?>(null) }

    // Rebind camera when lensFacing or flashMode changes
    fun bindCameraUseCases(pView: PreviewView) {
        val provider = cameraProvider ?: return
        try {
            provider.unbindAll()

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.surfaceProvider = pView.surfaceProvider
                }

            val capture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .setFlashMode(flashMode)
                .build()

            imageCapture = capture

            val cam = provider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                capture
            )

            camera = cam
            cameraControl = cam.cameraControl
            captureError = null
        } catch (e: Exception) {
            captureError = "Camera binding error: ${e.localizedMessage ?: "Device camera unavailable"}"
        }
    }

    LaunchedEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                previewViewInstance?.let { bindCameraUseCases(it) }
            } catch (e: Exception) {
                captureError = "Unable to start CameraX: ${e.localizedMessage}"
            }
        }, ContextCompat.getMainExecutor(context))
    }

    LaunchedEffect(lensFacing, flashMode) {
        previewViewInstance?.let { bindCameraUseCases(it) }
    }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            cameraProvider?.unbindAll()
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("camerax_scanner_view")
    ) {
        val containerWidth = maxWidth
        val containerHeight = maxHeight
        val density = LocalDensity.current

        // 1. CameraX Preview View
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    previewViewInstance = this
                    cameraProvider?.let { bindCameraUseCases(this) }
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        tapFocusPoint = offset
                        val pView = previewViewInstance
                        if (pView != null && cameraControl != null) {
                            try {
                                val factory = SurfaceOrientedMeteringPointFactory(
                                    pView.width.toFloat(),
                                    pView.height.toFloat()
                                )
                                val point = factory.createPoint(offset.x, offset.y)
                                val action = FocusMeteringAction.Builder(point).build()
                                cameraControl?.startFocusAndMetering(action)
                            } catch (_: Exception) {}
                        }
                    }
                }
        )

        // Reset tap focus ring after animation
        LaunchedEffect(tapFocusPoint) {
            if (tapFocusPoint != null) {
                delay(1200)
                tapFocusPoint = null
            }
        }

        // Tap-to-focus ring animation
        tapFocusPoint?.let { point ->
            TapFocusRing(offset = point)
        }

        // 2. High-precision Document Framing Viewfinder
        DocumentFramingOverlay(
            modifier = Modifier.fillMaxSize()
        )

        // 3. Top Toolbar: Close, Flash toggle, Lens switch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Close button
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                    .testTag("camerax_close_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Camera",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Document Scan Mode Pill
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.Black.copy(alpha = 0.55f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF6366F1))
                    )
                    Text(
                        text = "DOCUMENT HD",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        ),
                        color = Color.White
                    )
                }
            }

            // Flash & Camera controls
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Flash mode toggle
                IconButton(
                    onClick = {
                        flashMode = when (flashMode) {
                            ImageCapture.FLASH_MODE_AUTO -> ImageCapture.FLASH_MODE_ON
                            ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_OFF
                            else -> ImageCapture.FLASH_MODE_AUTO
                        }
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                        .testTag("camerax_flash_toggle")
                ) {
                    Icon(
                        imageVector = when (flashMode) {
                            ImageCapture.FLASH_MODE_ON -> Icons.Default.FlashOn
                            ImageCapture.FLASH_MODE_OFF -> Icons.Default.FlashOff
                            else -> Icons.Default.FlashAuto
                        },
                        contentDescription = "Toggle Flash Mode",
                        tint = if (flashMode == ImageCapture.FLASH_MODE_OFF) Color.White.copy(alpha = 0.7f) else Color(0xFFFBBF24),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Switch Camera Lens
                IconButton(
                    onClick = {
                        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                            CameraSelector.LENS_FACING_FRONT
                        } else {
                            CameraSelector.LENS_FACING_BACK
                        }
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                        .testTag("camerax_switch_lens_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Cameraswitch,
                        contentDescription = "Switch Camera Lens",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 4. Capture Error Banner if hardware issue
        if (captureError != null) {
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1E293B),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Camera Unavailable",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = captureError ?: "Could not open camera sensor",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF94A3B8),
                            textAlign = TextAlign.Center
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onPickFromGallery,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
                    ) {
                        Text("Pick Document File Instead")
                    }
                }
            }
        }

        // 5. Bottom Control Deck (Mode Switcher, Filter Pills, Shutter, Gallery, Batch Actions)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Live capture status toast message
            AnimatedVisibility(
                visible = statusToastMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF10B981),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = statusToastMessage ?: "",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }

            // Mode Selector Pill (Single Page vs Continuous Batch 100-Pages)
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.Black.copy(alpha = 0.75f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Single Page Pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (!isBatchMode) Color(0xFF4F46E5) else Color.Transparent,
                        modifier = Modifier.clickable { isBatchMode = false }
                    ) {
                        Text(
                            text = "Single Page",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = if (!isBatchMode) FontWeight.Bold else FontWeight.Normal,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }

                    // Batch Mode Pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isBatchMode) Color(0xFF4F46E5) else Color.Transparent,
                        modifier = Modifier.clickable { isBatchMode = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Batch Mode (100 Pgs)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = if (isBatchMode) FontWeight.Bold else FontWeight.Normal,
                                    color = Color.White
                                )
                            )
                            if (batchPages.isNotEmpty()) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF10B981)
                                ) {
                                    Text(
                                        text = "${batchPages.size}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        ),
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // On-Device Filter Chips Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(DocumentFilterMode.values()) { filter ->
                    val isSelected = selectedFilter == filter
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) Color(0xFF4F46E5) else Color.Black.copy(alpha = 0.55f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFF818CF8) else Color.White.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.clickable { selectedFilter = filter }
                    ) {
                        Text(
                            text = filter.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Shutter deck row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Left Action: Gallery Picker
                IconButton(
                    onClick = onPickFromGallery,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .testTag("camerax_gallery_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Pick Document from Files",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Master High-Res Shutter Button
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(4.dp, Color.White, CircleShape)
                        .padding(5.dp)
                        .clip(CircleShape)
                        .background(if (isCapturing) Color.Gray else Color.White)
                        .clickable(enabled = !isCapturing && imageCapture != null) {
                            val capture = imageCapture ?: return@clickable
                            isCapturing = true

                            val photoFile = File(
                                context.cacheDir,
                                "doc_scan_${System.currentTimeMillis()}.jpg"
                            )
                            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                            capture.takePicture(
                                outputOptions,
                                ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                        coroutineScope.launch(Dispatchers.Default) {
                                            try {
                                                val rawBitmap = processCapturedPhoto(photoFile)
                                                // 1. Local on-device edge detection
                                                val quad = LocalDocumentProcessor.detectDocumentEdges(rawBitmap)
                                                // 2. Crop and rectify perspective
                                                val cropped = LocalDocumentProcessor.cropAndRectifyDocument(rawBitmap, quad)
                                                // 3. Compress & enhance locally to ~150KB
                                                val processed = DocumentCompressor.processAndCompress(
                                                    context = context,
                                                    inputBitmap = cropped,
                                                    filterMode = selectedFilter,
                                                    outputFileNamePrefix = if (isBatchMode) "batch_p${batchPages.size + 1}" else "scan_doc"
                                                )

                                                withContext(Dispatchers.Main) {
                                                    isCapturing = false
                                                    if (isBatchMode) {
                                                        val newPage = BatchScannedPage(
                                                            pageNumber = batchPages.size + 1,
                                                            bitmap = processed.bitmap,
                                                            file = processed.compressedFile,
                                                            originalSizeBytes = processed.stats.originalSizeBytes,
                                                            compressedSizeBytes = processed.stats.compressedSizeBytes
                                                        )
                                                        batchPages.add(newPage)
                                                        statusToastMessage = "Page ${batchPages.size} captured • ${processed.stats.formattedCompressedSize} (${String.format("%.0f", processed.stats.reductionPercentage)}% saved)"
                                                    } else {
                                                        val uri = Uri.fromFile(processed.compressedFile)
                                                        onPhotoCaptured(processed.bitmap, uri)
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                withContext(Dispatchers.Main) {
                                                    isCapturing = false
                                                    captureError = "Processing error: ${e.localizedMessage}"
                                                }
                                            }
                                        }
                                    }

                                    override fun onError(exc: ImageCaptureException) {
                                        isCapturing = false
                                        captureError = "Capture failed: ${exc.localizedMessage}"
                                    }
                                }
                            )
                        }
                        .testTag("camerax_shutter_button"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCapturing) {
                        CircularProgressIndicator(
                            color = Color(0xFF4F46E5),
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(32.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isBatchMode && batchPages.isNotEmpty()) {
                                Text(
                                    text = "${batchPages.size}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4F46E5)
                                    )
                                )
                            }
                        }
                    }
                }

                // Right Action: In Batch Mode show Finish Button, otherwise Scan Guide
                if (isBatchMode && batchPages.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF10B981),
                        modifier = Modifier
                            .clickable { showBatchReviewDialog = true }
                            .testTag("finish_batch_scan_button")
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Review Batch",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Done (${batchPages.size})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Document Scan",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Batch Review & Queue Dialog
        if (showBatchReviewDialog && batchPages.isNotEmpty()) {
            BatchReviewDialog(
                batchPages = batchPages,
                selectedFilter = selectedFilter,
                onDeletePage = { page -> batchPages.remove(page) },
                onQueueAllOffline = {
                    showBatchReviewDialog = false
                    val pages = batchPages.map { it.bitmap to it.file }
                    onBatchQueued?.invoke(pages, selectedFilter)
                    onClose()
                },
                onParseFirstPage = {
                    showBatchReviewDialog = false
                    val first = batchPages.first()
                    onPhotoCaptured(first.bitmap, Uri.fromFile(first.file))
                },
                onDismiss = { showBatchReviewDialog = false }
            )
        }
    }
}

@Composable
private fun BatchReviewDialog(
    batchPages: List<BatchScannedPage>,
    selectedFilter: DocumentFilterMode,
    onDeletePage: (BatchScannedPage) -> Unit,
    onQueueAllOffline: () -> Unit,
    onParseFirstPage: () -> Unit,
    onDismiss: () -> Unit
) {
    val totalOriginalBytes = batchPages.sumOf { it.originalSizeBytes }
    val totalCompressedBytes = batchPages.sumOf { it.compressedSizeBytes }
    val savedPercentage = if (totalOriginalBytes > 0) {
        ((totalOriginalBytes - totalCompressedBytes).toFloat() / totalOriginalBytes) * 100f
    } else 0f

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Batch Scanned (${batchPages.size} Pages)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Enhanced with ${selectedFilter.label}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Compression metrics summary card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF0FDF4),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCFCE7)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "DATA FOOTPRINT REDUCTION",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = Color(0xFF16A34A)
                            )
                            Text(
                                text = "${String.format("%.1f", totalCompressedBytes / 1024f)} KB total (${String.format("%.0f", savedPercentage)}% saved)",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                ),
                                color = Color(0xFF15803D)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF16A34A)
                        ) {
                            Text(
                                text = "~150KB Target",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Horizontal list of scanned page thumbnails
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(batchPages) { page ->
                        Card(
                            modifier = Modifier
                                .width(90.dp)
                                .height(130.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                androidx.compose.foundation.Image(
                                    bitmap = androidx.compose.ui.graphics.asImageBitmap(page.bitmap),
                                    contentDescription = "Page ${page.pageNumber}",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )

                                Surface(
                                    shape = RoundedCornerShape(bottomEnd = 6.dp),
                                    color = Color.Black.copy(alpha = 0.7f),
                                    modifier = Modifier.align(Alignment.TopStart)
                                ) {
                                    Text(
                                        text = "P.${page.pageNumber}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        ),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { onDeletePage(page) },
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.TopEnd)
                                        .background(Color.Red.copy(alpha = 0.8f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Page",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(topStart = 6.dp),
                                    color = Color.Black.copy(alpha = 0.7f),
                                    modifier = Modifier.align(Alignment.BottomEnd)
                                ) {
                                    Text(
                                        text = "${page.compressedSizeBytes / 1024}K",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 8.5.sp,
                                            color = Color.White
                                        ),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action 1: Queue All Offline for background sync
                Button(
                    onClick = onQueueAllOffline,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("queue_all_batch_offline_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4F46E5),
                        contentColor = Color.White
                    )
                ) {
                    Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Queue All (${batchPages.size} Pgs) for Offline Sync",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action 2: Parse Page 1 Immediately
                OutlinedButton(
                    onClick = onParseFirstPage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Text(
                        text = "Parse Page 1 Now in Engine",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            color = Color(0xFF334155),
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}
    }
}

@Composable
private fun TapFocusRing(offset: Offset) {
    var animatedScale by remember { mutableStateOf(1.4f) }
    var animatedAlpha by remember { mutableStateOf(1f) }

    LaunchedEffect(offset) {
        animatedScale = 1.0f
        delay(600)
        animatedAlpha = 0f
    }

    val scale by animateFloatAsState(
        targetValue = animatedScale,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "focus_scale"
    )

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {}
    ) {
        if (animatedAlpha > 0f) {
            val ringRadius = 36.dp.toPx() * scale
            drawCircle(
                color = Color(0xFFFBBF24).copy(alpha = animatedAlpha),
                radius = ringRadius,
                center = offset,
                style = Stroke(width = 2.5.dp.toPx())
            )
            // Center crosshair tick marks
            val tick = 6.dp.toPx()
            drawLine(
                color = Color(0xFFFBBF24).copy(alpha = animatedAlpha),
                start = Offset(offset.x - ringRadius - tick, offset.y),
                end = Offset(offset.x - ringRadius, offset.y),
                strokeWidth = 2.dp.toPx()
            )
            drawLine(
                color = Color(0xFFFBBF24).copy(alpha = animatedAlpha),
                start = Offset(offset.x + ringRadius, offset.y),
                end = Offset(offset.x + ringRadius + tick, offset.y),
                strokeWidth = 2.dp.toPx()
            )
            drawLine(
                color = Color(0xFFFBBF24).copy(alpha = animatedAlpha),
                start = Offset(offset.x, offset.y - ringRadius - tick),
                end = Offset(offset.x, offset.y - ringRadius),
                strokeWidth = 2.dp.toPx()
            )
            drawLine(
                color = Color(0xFFFBBF24).copy(alpha = animatedAlpha),
                start = Offset(offset.x, offset.y + ringRadius),
                end = Offset(offset.x, offset.y + ringRadius + tick),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}

@Composable
private fun DocumentFramingOverlay(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val strokeWidth = 3.5.dp.toPx()
        val cornerLength = 36.dp.toPx()
        val cornerRadius = 16.dp.toPx()

        // Calculate document framing rectangle (proportional to standard A4/Letter/Receipt, ~1:1.38 aspect ratio)
        val frameWidth = size.width * 0.88f
        val frameHeight = frameWidth * 1.35f
        val topOffset = (size.height - frameHeight) / 2f - 30.dp.toPx()
        val leftOffset = (size.width - frameWidth) / 2f

        val frameRect = Rect(
            left = leftOffset,
            top = topOffset.coerceAtLeast(80.dp.toPx()),
            right = leftOffset + frameWidth,
            bottom = (topOffset + frameHeight).coerceAtMost(size.height - 180.dp.toPx())
        )

        // Darken outside viewfinder cutout using semi-transparent overlay
        val overlayPath = Path().apply {
            // Full screen
            addRect(Rect(0f, 0f, size.width, size.height))
            // Cutout
            addRoundRect(
                RoundRect(
                    rect = frameRect,
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                )
            )
            fillType = androidx.compose.ui.graphics.PathFillType.EvenOdd
        }

        drawPath(
            path = overlayPath,
            color = Color.Black.copy(alpha = 0.45f)
        )

        // Draw High-Contrast Corner Brackets (Indigo / Bright White)
        val bracketColor = Color(0xFF818CF8) // Indigo-400

        // Top-Left Corner
        drawPath(
            path = Path().apply {
                moveTo(frameRect.left, frameRect.top + cornerLength)
                lineTo(frameRect.left, frameRect.top + cornerRadius)
                quadraticTo(
                    frameRect.left,
                    frameRect.top,
                    frameRect.left + cornerRadius,
                    frameRect.top
                )
                lineTo(frameRect.left + cornerLength, frameRect.top)
            },
            color = bracketColor,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Top-Right Corner
        drawPath(
            path = Path().apply {
                moveTo(frameRect.right - cornerLength, frameRect.top)
                lineTo(frameRect.right - cornerRadius, frameRect.top)
                quadraticTo(
                    frameRect.right,
                    frameRect.top,
                    frameRect.right,
                    frameRect.top + cornerRadius
                )
                lineTo(frameRect.right, frameRect.top + cornerLength)
            },
            color = bracketColor,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Bottom-Left Corner
        drawPath(
            path = Path().apply {
                moveTo(frameRect.left, frameRect.bottom - cornerLength)
                lineTo(frameRect.left, frameRect.bottom - cornerRadius)
                quadraticTo(
                    frameRect.left,
                    frameRect.bottom,
                    frameRect.left + cornerRadius,
                    frameRect.bottom
                )
                lineTo(frameRect.left + cornerLength, frameRect.bottom)
            },
            color = bracketColor,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Bottom-Right Corner
        drawPath(
            path = Path().apply {
                moveTo(frameRect.right - cornerLength, frameRect.bottom)
                lineTo(frameRect.right - cornerRadius, frameRect.bottom)
                quadraticTo(
                    frameRect.right,
                    frameRect.bottom,
                    frameRect.right,
                    frameRect.bottom - cornerRadius
                )
                lineTo(frameRect.right, frameRect.bottom - cornerLength)
            },
            color = bracketColor,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

/**
 * Decodes high-resolution captured JPEG with correct EXIF orientation
 * and safe downsampling to prevent OOM on extreme resolution camera sensors.
 */
private fun processCapturedPhoto(photoFile: File): Bitmap {
    val exif = try {
        android.media.ExifInterface(photoFile.absolutePath)
    } catch (_: Exception) {
        null
    }

    val orientation = exif?.getAttributeInt(
        android.media.ExifInterface.TAG_ORIENTATION,
        android.media.ExifInterface.ORIENTATION_UNDEFINED
    ) ?: android.media.ExifInterface.ORIENTATION_UNDEFINED

    val rotationDegrees = when (orientation) {
        android.media.ExifInterface.ORIENTATION_ROTATE_90 -> 90f
        android.media.ExifInterface.ORIENTATION_ROTATE_180 -> 180f
        android.media.ExifInterface.ORIENTATION_ROTATE_270 -> 270f
        else -> 0f
    }

    val boundsOptions = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    BitmapFactory.decodeFile(photoFile.absolutePath, boundsOptions)

    // Max 2560px on longest dimension ensures razor-sharp OCR while avoiding OOM
    val maxDimension = 2560
    var sampleSize = 1
    while (boundsOptions.outWidth / sampleSize > maxDimension || boundsOptions.outHeight / sampleSize > maxDimension) {
        sampleSize *= 2
    }

    val decodeOptions = BitmapFactory.Options().apply {
        inSampleSize = sampleSize
        inPreferredConfig = Bitmap.Config.ARGB_8888
    }

    val rawBitmap = BitmapFactory.decodeFile(photoFile.absolutePath, decodeOptions)
        ?: throw IllegalStateException("Failed to decode captured document photo")

    return if (rotationDegrees != 0f) {
        val matrix = Matrix().apply { postRotate(rotationDegrees) }
        val rotated = Bitmap.createBitmap(
            rawBitmap,
            0,
            0,
            rawBitmap.width,
            rawBitmap.height,
            matrix,
            true
        )
        if (rotated != rawBitmap) {
            rawBitmap.recycle()
        }
        rotated
    } else {
        rawBitmap
    }
}
