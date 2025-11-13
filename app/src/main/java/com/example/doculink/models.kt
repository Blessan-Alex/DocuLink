package com.example.doculink

import com.google.gson.annotations.SerializedName

data class QueryRequest(
    val query: String,
    val department: String? = null,
    @SerializedName("top_k") val topK: Int = 5,
    @SerializedName("search_type") val searchType: String = "hybrid"
)

data class ContextDoc(
    @SerializedName("document_id") val documentId: String = "Unknown",
    @SerializedName("chunk_index") val chunkIndex: Int = 0,
    val text: String = "",
    @SerializedName("similarity_score") val similarityScore: Double = 0.0,
    val department: String? = "Unknown"
)

data class QueryResponse(
    val query: String,
    val response: String,
    @SerializedName("search_type") val searchType: String,
    @SerializedName("context_documents") val contextDocuments: List<ContextDoc>,
    @SerializedName("search_time") val searchTime: Double,
    @SerializedName("total_documents_searched") val totalDocumentsSeearched: Int,
    @SerializedName("department_filter") val departmentFilter: String? = null
)
