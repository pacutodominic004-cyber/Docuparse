package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ParsedDocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {

    @Query("SELECT * FROM parsed_documents ORDER BY timestamp DESC")
    fun getAllDocuments(): Flow<List<ParsedDocumentEntity>>

    @Query("SELECT * FROM parsed_documents WHERE id = :id")
    fun getDocumentById(id: Long): Flow<ParsedDocumentEntity?>

    @Query("SELECT * FROM parsed_documents WHERE category = :category ORDER BY timestamp DESC")
    fun getDocumentsByCategory(category: String): Flow<List<ParsedDocumentEntity>>

    @Query("""
        SELECT * FROM parsed_documents 
        WHERE organizationName LIKE '%' || :query || '%' 
           OR referenceNumber LIKE '%' || :query || '%' 
           OR detectedType LIKE '%' || :query || '%' 
           OR rawJson LIKE '%' || :query || '%'
        ORDER BY timestamp DESC
    """)
    fun searchDocuments(query: String): Flow<List<ParsedDocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: ParsedDocumentEntity): Long

    @Query("DELETE FROM parsed_documents WHERE id = :id")
    suspend fun deleteDocumentById(id: Long)

    @Query("DELETE FROM parsed_documents")
    suspend fun deleteAllDocuments()
}
