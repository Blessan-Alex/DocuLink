package com.example.doculink



data class DocumentItem(
    val id: String = "",           // Firestore document id
    val title: String = "",
    val tag: String = "",
    val description: String = "",
    val date: String = "",
    val size: String = "",
    val status: String = ""        // "embedding_in_progress", "ready", "failed" etc.
)



