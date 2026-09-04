package com.example.data.local

import com.example.data.model.ParsedDocumentEntity
import com.example.data.model.QueuedDocumentEntity
import com.example.data.model.SyncQueueStatus
import kotlinx.coroutines.flow.Flow

class DocumentRepository(
    private val documentDao: DocumentDao,
    private val queuedDocumentDao: QueuedDocumentDao
) {

    val allDocuments: Flow<List<ParsedDocumentEntity>> = documentDao.getAllDocuments()
    val allQueuedDocuments: Flow<List<QueuedDocumentEntity>> = queuedDocumentDao.getAllQueuedDocuments()
    val pendingQueueCount: Flow<Int> = queuedDocumentDao.getPendingQueueCount()

    fun getDocumentById(id: Long): Flow<ParsedDocumentEntity?> = documentDao.getDocumentById(id)

    fun getDocumentsByCategory(category: String): Flow<List<ParsedDocumentEntity>> =
        documentDao.getDocumentsByCategory(category)

    fun searchDocuments(query: String): Flow<List<ParsedDocumentEntity>> =
        documentDao.searchDocuments(query)

    suspend fun insert(document: ParsedDocumentEntity): Long =
        documentDao.insertDocument(document)

    suspend fun deleteById(id: Long) =
        documentDao.deleteDocumentById(id)

    suspend fun deleteAll() =
        documentDao.deleteAllDocuments()

    // Offline Queue Methods
    suspend fun insertQueuedDocument(doc: QueuedDocumentEntity): Long =
        queuedDocumentDao.insertQueuedDocument(doc)

    suspend fun insertAllQueued(docs: List<QueuedDocumentEntity>): List<Long> =
        queuedDocumentDao.insertAll(docs)

    suspend fun updateQueuedStatus(id: Long, status: SyncQueueStatus, error: String? = null) =
        queuedDocumentDao.updateStatus(id, status, error)

    suspend fun updateQueuedDocument(doc: QueuedDocumentEntity) =
        queuedDocumentDao.update(doc)

    suspend fun deleteQueuedById(id: Long) =
        queuedDocumentDao.deleteById(id)

    suspend fun clearSyncedQueue() =
        queuedDocumentDao.clearSynced()

    suspend fun clearAllQueue() =
        queuedDocumentDao.clearAll()
}
