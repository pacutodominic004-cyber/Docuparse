package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.engine.GeminiParserEngine
import com.example.data.local.AppDatabase
import com.example.data.local.DocumentRepository
import com.example.data.model.DocumentCategory
import com.example.data.model.ParsedDocumentEntity
import com.example.data.model.ParsedDocumentResult
import com.example.data.model.QueuedDocumentEntity
import com.example.data.model.SamplePreset
import com.example.data.model.SamplePresetsData
import com.example.data.model.SyncQueueStatus
import com.example.util.CompressionStats
import com.example.util.DocumentCompressor
import com.example.util.DocumentFilterMode
import com.example.util.NetworkMonitor
import com.example.util.NetworkStatus
import com.example.util.SampleImageGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

data class ParserUiState(
    val selectedBitmap: Bitmap? = null,
    val selectedImageUri: Uri? = null,
    val activePreset: SamplePreset? = null,
    val isParsing: Boolean = false,
    val parsingStatusMessage: String = "",
    val currentResult: ParsedDocumentResult? = null,
    val currentEntityId: Long? = null,
    val errorMessage: String? = null,
    val customApiKey: String = "",
    val activeTab: Int = 0, // 0: Engine, 1: Archive, 2: Benchmark Presets, 3: Offline Queue
    val activeFilterMode: DocumentFilterMode = DocumentFilterMode.MONOCHROME_HIGH_CONTRAST,
    val latestCompressionStats: CompressionStats? = null,
    val isSyncingQueue: Boolean = false,
    val queueSuccessMessage: String? = null
)

class DocumentParserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DocumentRepository
    private val parserEngine: GeminiParserEngine
    private val networkMonitor: NetworkMonitor = NetworkMonitor(application)

    val networkStatus: StateFlow<NetworkStatus> = networkMonitor.networkStatus

    private val _uiState = MutableStateFlow(ParserUiState())
    val uiState: StateFlow<ParserUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(DocumentCategory.ALL)
    val selectedCategory: StateFlow<DocumentCategory> = _selectedCategory.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = DocumentRepository(db.documentDao(), db.queuedDocumentDao())
        parserEngine = GeminiParserEngine(application)

        // Load the first preset by default so the user immediately sees a rich East African SACCO document
        loadPreset(SamplePresetsData.presets[0], autoParse = true)

        // Automatically sync queued documents when network connectivity is restored
        viewModelScope.launch {
            networkStatus.collect { status ->
                if (status.isOnline && !_uiState.value.isSyncingQueue) {
                    val pending = repository.pendingQueueCount
                    // If there are pending items queued offline, trigger background sync
                    syncOfflineQueue()
                }
            }
        }
    }

    val queuedDocuments: StateFlow<List<QueuedDocumentEntity>> = repository.allQueuedDocuments.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val pendingQueueCount: StateFlow<Int> = repository.pendingQueueCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val historicalDocuments: StateFlow<List<ParsedDocumentEntity>> = combine(
        repository.allDocuments,
        _searchQuery,
        _selectedCategory
    ) { docs, query, category ->
        var filtered = docs
        if (category != DocumentCategory.ALL) {
            val categoryLabel = when (category) {
                DocumentCategory.SACCO -> "SACCO"
                DocumentCategory.SCHOOL -> "School"
                DocumentCategory.FLEET_CPA -> "Fleet"
                else -> ""
            }
            filtered = filtered.filter { it.category.contains(categoryLabel, ignoreCase = true) }
        }
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            filtered = filtered.filter {
                (it.organizationName?.lowercase()?.contains(q) == true) ||
                (it.referenceNumber?.lowercase()?.contains(q) == true) ||
                it.detectedType.lowercase().contains(q) ||
                it.rawJson.lowercase().contains(q)
            }
        }
        filtered
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setActiveTab(tab: Int) {
        _uiState.value = _uiState.value.copy(activeTab = tab)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: DocumentCategory) {
        _selectedCategory.value = category
    }

    fun setCustomApiKey(key: String) {
        _uiState.value = _uiState.value.copy(customApiKey = key)
    }

    fun loadPreset(preset: SamplePreset, autoParse: Boolean = true) {
        val bitmap = SampleImageGenerator.generatePresetBitmap(preset.id)
        _uiState.value = _uiState.value.copy(
            selectedBitmap = bitmap,
            selectedImageUri = null,
            activePreset = preset,
            errorMessage = null,
            currentResult = null,
            currentEntityId = null
        )

        if (autoParse) {
            parseCurrentDocument(presetId = preset.id)
        }
    }

    fun onImageSelected(uri: Uri) {
        val bitmap = parserEngine.loadBitmapFromUri(uri)
        if (bitmap != null) {
            _uiState.value = _uiState.value.copy(
                selectedBitmap = bitmap,
                selectedImageUri = uri,
                activePreset = null,
                errorMessage = null,
                currentResult = null,
                currentEntityId = null,
                activeTab = 0
            )
            parseCurrentDocument()
        } else {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Failed to load image from selected source."
            )
        }
    }

    fun onDocumentCaptured(bitmap: Bitmap, uri: Uri? = null) {
        _uiState.value = _uiState.value.copy(
            selectedBitmap = bitmap,
            selectedImageUri = uri,
            activePreset = null,
            errorMessage = null,
            currentResult = null,
            currentEntityId = null,
            activeTab = 0
        )
        parseCurrentDocument(presetId = null)
    }

    fun parseCurrentDocument(presetId: String? = _uiState.value.activePreset?.id) {
        val bitmap = _uiState.value.selectedBitmap ?: return

        _uiState.value = _uiState.value.copy(
            isParsing = true,
            parsingStatusMessage = "Analyzing document, transcribing handwriting, and parsing tables...",
            errorMessage = null
        )

        viewModelScope.launch {
            val result = parserEngine.parseDocumentImage(
                bitmap = bitmap,
                customApiKey = _uiState.value.customApiKey,
                presetId = presetId
            )

            result.onSuccess { parsed ->
                // Infer category from detected type
                val categoryName = when {
                    parsed.metadata.detectedType.contains("SACCO", ignoreCase = true) ||
                    parsed.metadata.detectedType.contains("Saving", ignoreCase = true) ||
                    parsed.metadata.detectedType.contains("Loan", ignoreCase = true) -> "SACCO"

                    parsed.metadata.detectedType.contains("School", ignoreCase = true) ||
                    parsed.metadata.detectedType.contains("Exam", ignoreCase = true) ||
                    parsed.metadata.detectedType.contains("CAT", ignoreCase = true) -> "School"

                    parsed.metadata.detectedType.contains("Fleet", ignoreCase = true) ||
                    parsed.metadata.detectedType.contains("Fuel", ignoreCase = true) ||
                    parsed.metadata.detectedType.contains("Invoice", ignoreCase = true) ||
                    parsed.metadata.detectedType.contains("EFRIS", ignoreCase = true) ||
                    parsed.metadata.detectedType.contains("Tax", ignoreCase = true) -> "Fleet & CPA"

                    else -> "Business"
                }

                // Persist to Room database
                val entity = ParsedDocumentEntity(
                    imageUri = _uiState.value.selectedImageUri?.toString(),
                    detectedType = parsed.metadata.detectedType,
                    confidenceScore = parsed.metadata.confidenceScore,
                    primaryDate = parsed.metadata.primaryDate,
                    organizationName = parsed.metadata.organizationName,
                    referenceNumber = parsed.metadata.referenceNumber,
                    category = categoryName,
                    rawJson = parsed.rawJson,
                    keyValuesCount = parsed.extractedKeyValues.size,
                    tabularRowCount = parsed.tabularData.size,
                    anomalyCount = parsed.flaggedAnomalies.size
                )
                val insertedId = repository.insert(entity)

                _uiState.value = _uiState.value.copy(
                    isParsing = false,
                    parsingStatusMessage = "Parsed successfully!",
                    currentResult = parsed,
                    currentEntityId = insertedId,
                    errorMessage = null
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isParsing = false,
                    parsingStatusMessage = "",
                    errorMessage = error.message ?: "Failed to parse document"
                )
            }
        }
    }

    fun viewHistoricalDocument(entity: ParsedDocumentEntity) {
        try {
            val parsed = ParsedDocumentResult.fromJson(entity.rawJson)
            // If it matches one of our presets, load that preset bitmap
            val matchedPreset = SamplePresetsData.presets.find { it.detectedType == entity.detectedType }
            val bitmap = if (matchedPreset != null) {
                SampleImageGenerator.generatePresetBitmap(matchedPreset.id)
            } else {
                _uiState.value.selectedBitmap
            }

            _uiState.value = _uiState.value.copy(
                selectedBitmap = bitmap,
                activePreset = matchedPreset,
                currentResult = parsed,
                currentEntityId = entity.id,
                activeTab = 0,
                errorMessage = null
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Failed to load record: ${e.message}"
            )
        }
    }

    fun deleteDocument(id: Long) {
        viewModelScope.launch {
            repository.deleteById(id)
            if (_uiState.value.currentEntityId == id) {
                _uiState.value = _uiState.value.copy(
                    currentResult = null,
                    currentEntityId = null
                )
            }
        }
    }

    fun applyFilterMode(mode: DocumentFilterMode) {
        val currentBitmap = _uiState.value.selectedBitmap ?: return
        viewModelScope.launch(Dispatchers.Default) {
            val processed = DocumentCompressor.processAndCompress(
                context = getApplication(),
                inputBitmap = currentBitmap,
                filterMode = mode
            )
            withContext(Dispatchers.Main) {
                _uiState.value = _uiState.value.copy(
                    activeFilterMode = mode,
                    selectedBitmap = processed.bitmap,
                    latestCompressionStats = processed.stats
                )
            }
        }
    }

    fun queueCurrentDocumentOffline(filterMode: DocumentFilterMode = _uiState.value.activeFilterMode) {
        val bitmap = _uiState.value.selectedBitmap ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val processed = DocumentCompressor.processAndCompress(
                    context = getApplication(),
                    inputBitmap = bitmap,
                    filterMode = filterMode,
                    outputFileNamePrefix = "queued_doc"
                )

                val entity = QueuedDocumentEntity(
                    batchId = "single_${System.currentTimeMillis()}",
                    pageIndex = 0,
                    cachedImagePath = processed.compressedFile.absolutePath,
                    originalSizeBytes = processed.stats.originalSizeBytes,
                    compressedSizeBytes = processed.stats.compressedSizeBytes,
                    filterMode = filterMode.label,
                    detectedType = _uiState.value.currentResult?.metadata?.detectedType ?: "Pending Offline Queue",
                    organizationName = _uiState.value.currentResult?.metadata?.organizationName,
                    syncStatus = SyncQueueStatus.QUEUED_OFFLINE
                )
                repository.insertQueuedDocument(entity)

                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        latestCompressionStats = processed.stats,
                        queueSuccessMessage = "Document compressed to ${processed.stats.formattedCompressedSize} (${String.format("%.0f", processed.stats.reductionPercentage)}% data saved) and queued locally."
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Failed to queue document: ${e.message}"
                    )
                }
            }
        }
    }

    fun queueBatchDocumentsOffline(
        batchPages: List<Pair<Bitmap, File>>,
        filterMode: DocumentFilterMode = _uiState.value.activeFilterMode
    ) {
        if (batchPages.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val batchId = "batch_${System.currentTimeMillis()}"
                var totalOriginal = 0L
                var totalCompressed = 0L

                val entities = batchPages.mapIndexed { index, (bitmap, file) ->
                    val processed = DocumentCompressor.processAndCompress(
                        context = getApplication(),
                        inputBitmap = bitmap,
                        filterMode = filterMode,
                        outputFileNamePrefix = "batch_p${index + 1}"
                    )
                    totalOriginal += processed.stats.originalSizeBytes
                    totalCompressed += processed.stats.compressedSizeBytes

                    QueuedDocumentEntity(
                        batchId = batchId,
                        pageIndex = index,
                        cachedImagePath = processed.compressedFile.absolutePath,
                        originalSizeBytes = processed.stats.originalSizeBytes,
                        compressedSizeBytes = processed.stats.compressedSizeBytes,
                        filterMode = filterMode.label,
                        detectedType = "Batch Page ${index + 1}",
                        syncStatus = SyncQueueStatus.QUEUED_OFFLINE
                    )
                }

                repository.insertAllQueued(entities)

                withContext(Dispatchers.Main) {
                    val savedPercent = if (totalOriginal > 0) ((totalOriginal - totalCompressed).toFloat() / totalOriginal) * 100f else 0f
                    val compSizeMb = totalCompressed.toFloat() / (1024 * 1024)
                    _uiState.value = _uiState.value.copy(
                        queueSuccessMessage = "Queued ${batchPages.size} batch pages locally! Total size: ${String.format("%.2f", compSizeMb)} MB (${String.format("%.0f", savedPercent)}% storage saved). Ready for background sync.",
                        activeTab = 0
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Batch queue error: ${e.message}"
                    )
                }
            }
        }
    }

    fun syncOfflineQueue() {
        if (_uiState.value.isSyncingQueue) return
        val isOnline = networkStatus.value.isOnline
        if (!isOnline) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Device is currently offline. Connect to Wi-Fi or mobile data to sync queued documents."
            )
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isSyncingQueue = true)
            try {
                val pending = queuedDocuments.value.filter {
                    it.syncStatus == SyncQueueStatus.QUEUED_OFFLINE || it.syncStatus == SyncQueueStatus.FAILED
                }

                for (doc in pending) {
                    repository.updateQueuedStatus(doc.id, SyncQueueStatus.PROCESSING_LOCAL)

                    val file = File(doc.cachedImagePath)
                    if (!file.exists()) {
                        repository.updateQueuedStatus(doc.id, SyncQueueStatus.FAILED, "Cached file missing")
                        continue
                    }

                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    if (bitmap == null) {
                        repository.updateQueuedStatus(doc.id, SyncQueueStatus.FAILED, "Failed to decode image")
                        continue
                    }

                    val parseResult = parserEngine.parseDocumentImage(
                        bitmap = bitmap,
                        customApiKey = _uiState.value.customApiKey
                    )

                    parseResult.onSuccess { parsed ->
                        val categoryName = when {
                            parsed.metadata.detectedType.contains("SACCO", ignoreCase = true) ||
                            parsed.metadata.detectedType.contains("Saving", ignoreCase = true) -> "SACCO"
                            parsed.metadata.detectedType.contains("School", ignoreCase = true) -> "School"
                            parsed.metadata.detectedType.contains("Fleet", ignoreCase = true) ||
                            parsed.metadata.detectedType.contains("Tax", ignoreCase = true) -> "Fleet & CPA"
                            else -> "Business"
                        }

                        // Save into history database
                        val entity = ParsedDocumentEntity(
                            imageUri = doc.cachedImagePath,
                            detectedType = parsed.metadata.detectedType,
                            confidenceScore = parsed.metadata.confidenceScore,
                            primaryDate = parsed.metadata.primaryDate,
                            organizationName = parsed.metadata.organizationName,
                            referenceNumber = parsed.metadata.referenceNumber,
                            category = categoryName,
                            rawJson = parsed.rawJson,
                            keyValuesCount = parsed.extractedKeyValues.size,
                            tabularRowCount = parsed.tabularData.size,
                            anomalyCount = parsed.flaggedAnomalies.size
                        )
                        repository.insert(entity)

                        // Update queued status to synced
                        repository.updateQueuedDocument(
                            doc.copy(
                                syncStatus = SyncQueueStatus.SYNCED_CLOUD,
                                detectedType = parsed.metadata.detectedType,
                                organizationName = parsed.metadata.organizationName,
                                parsedRawJson = parsed.rawJson,
                                syncErrorMessage = null
                            )
                        )
                    }.onFailure { err ->
                        repository.updateQueuedStatus(doc.id, SyncQueueStatus.FAILED, err.message)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Queue sync error: ${e.message}"
                    )
                }
            } finally {
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(isSyncingQueue = false)
                }
            }
        }
    }

    fun retryQueuedItem(item: QueuedDocumentEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateQueuedStatus(item.id, SyncQueueStatus.QUEUED_OFFLINE)
            syncOfflineQueue()
        }
    }

    fun deleteQueuedItem(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteQueuedById(id)
        }
    }

    fun clearSyncedQueue() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearSyncedQueue()
        }
    }

    fun dismissQueueMessage() {
        _uiState.value = _uiState.value.copy(queueSuccessMessage = null)
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }
}
