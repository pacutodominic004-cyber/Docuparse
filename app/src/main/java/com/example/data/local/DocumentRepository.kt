package com.example.data.local

import com.example.data.model.ParsedDocumentEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow

class DocumentRepository(private val documentDao: DocumentDao) {

    // Initialize Firebase Firestore
    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    
    // Initialize Firebase Storage
    private val storage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    val allDocuments: Flow<List<ParsedDocumentEntity>> = documentDao.getAllDocuments()

    fun getDocumentById(id: Long): Flow<ParsedDocumentEntity?> = documentDao.getDocumentById(id)

    fun getDocumentsByCategory(category: String): Flow<List<ParsedDocumentEntity>> =
        documentDao.getDocumentsByCategory(category)

    fun searchDocuments(query: String): Flow<List<ParsedDocumentEntity>> =
        documentDao.searchDocuments(query)

    suspend fun insert(document: ParsedDocumentEntity): Long {
        val id = documentDao.insertDocument(document)
        val documentWithId = document.copy(id = id)
        syncDocumentToCloud(documentWithId)
        return id
    }
    
    private fun syncDocumentToCloud(document: ParsedDocumentEntity) {
        val docMap = hashMapOf(
            "id" to document.id,
            "timestamp" to document.timestamp,
            "detectedType" to document.detectedType,
            "confidenceScore" to document.confidenceScore,
            "primaryDate" to document.primaryDate,
            "organizationName" to document.organizationName,
            "referenceNumber" to document.referenceNumber,
            "category" to document.category,
            "rawJson" to document.rawJson,
            "keyValuesCount" to document.keyValuesCount,
            "tabularRowCount" to document.tabularRowCount,
            "anomalyCount" to document.anomalyCount
        )

        firestore.collection("parsed_documents")
            .document(document.id.toString())
            .set(docMap)
            .addOnSuccessListener {
                // Successfully backed up to Firestore
            }
            .addOnFailureListener { e ->
                // Failed to back up, will rely on eventual consistency or retry mechanisms
            }
    }

    suspend fun deleteById(id: Long) {
        documentDao.deleteDocumentById(id)
        firestore.collection("parsed_documents").document(id.toString()).delete()
    }

    suspend fun deleteAll() {
        documentDao.deleteAllDocuments()
        // Note: For a production app, bulk deletion in Firestore would be handled via a Callable Cloud Function
        // or a batch delete operation to avoid excessive client-side reads/writes.
    }
}
