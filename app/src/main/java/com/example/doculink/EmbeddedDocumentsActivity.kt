package com.example.doculink

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class EmbeddedDocumentsActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_embedded_documents)

        recycler = findViewById(R.id.recyclerDocuments)
        recycler.layoutManager = LinearLayoutManager(this)

        val demoDocs = listOf(
            DocumentItem(
                title = "Financial Invoice - Traction Motor",
                tag = "Financial",
                description = "Invoice for traction motor services and maintenance",
                date = "2025-01-15",
                size = "2.3 MB"
            ),
            DocumentItem(
                title = "Incident Report - Emergency Brake",
                tag = "Safety",
                description = "Detailed incident report for emergency brake failure",
                date = "2025-01-14",
                size = "1.8 MB"
            ),
            DocumentItem(
                title = "Incident Report - Signal Failure",
                tag = "Safety",
                description = "Signal system failure incident documentation",
                date = "2025-01-13",
                size = "2.1 MB"
            )
        )

        recycler.adapter = DocumentAdapter(demoDocs) { doc ->
            Toast.makeText(this, "Clicked: ${doc.title}", Toast.LENGTH_SHORT).show()
        }
    }
}



