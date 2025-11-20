package com.example.doculink

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class EmbeddedDocumentsActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: DocumentAdapter

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val storage by lazy { FirebaseStorage.getInstance() }

    private var listenerRegistration: ListenerRegistration? = null

    // File picker
    private val pickDocument =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            if (uri != null) {
                handlePickedDocument(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_embedded_documents)

        recycler = findViewById(R.id.recyclerDocuments)
        recycler.layoutManager = LinearLayoutManager(this)

        adapter = DocumentAdapter(emptyList()) { doc ->
            Toast.makeText(this, "Clicked: ${doc.title}", Toast.LENGTH_SHORT).show()
        }
        recycler.adapter = adapter

        findViewById<ImageButton>(R.id.btnAddDocument).setOnClickListener {
            openFilePicker()
        }

        listenForDocuments()
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration?.remove()
    }

    private fun openFilePicker() {
        pickDocument.launch(
            arrayOf(
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "image/*"
            )
        )
    }

    private fun handlePickedDocument(uri: Uri) {
        // 1) Get basic meta from ContentResolver
        val cursor = contentResolver.query(uri, null, null, null, null)
        var name = "Document"
        var sizeBytes: Long = 0

        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
            if (it.moveToFirst()) {
                if (nameIndex >= 0) name = it.getString(nameIndex) ?: name
                if (sizeIndex >= 0) sizeBytes = it.getLong(sizeIndex)
            }
        }

        val sizeMb = if (sizeBytes > 0) {
            String.format(Locale.US, "%.1f MB", sizeBytes / (1024f * 1024f))
        } else {
            "—"
        }

        val now = Date()
        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(now)

        // 2) Create a Firestore document first with "embedding_in_progress"
        val docRef = db.collection("documents").document()   // random id
        val docId = docRef.id

        val docData = hashMapOf(
            "id" to docId,
            "title" to name,
            "tag" to "Uncategorized",
            "description" to "Uploaded from mobile app",
            "date" to dateStr,
            "size" to sizeMb,
            "status" to "embedding_in_progress",
            "storagePath" to "",
            "createdAt" to now
        )

        docRef.set(docData)
            .addOnSuccessListener {
                // 👇 Toast right when the doc is registered
                Toast.makeText(
                    this,
                    "Embedding started for \"$name\".\nEstimated time ≈ 10 minutes.",
                    Toast.LENGTH_LONG
                ).show()

                // 3) Upload file to Firebase Storage
                uploadFileToStorage(uri, docId, name)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to create document: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun uploadFileToStorage(uri: Uri, docId: String, name: String) {
        val storageRef = storage.reference
            .child("documents/${docId}_${UUID.randomUUID()}_$name")

        val uploadTask = storageRef.putFile(uri)

        uploadTask
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    // 4) Update Firestore with storage path / URL
                    db.collection("documents").document(docId)
                        .update(
                            mapOf(
                                "storagePath" to storageRef.path,
                                "downloadUrl" to downloadUri.toString()
                                // status remains "embedding_in_progress"
                                // your backend will flip it to "ready"
                            )
                        )
                }
                Toast.makeText(this, "File uploaded. Embedding will complete soon.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                db.collection("documents").document(docId)
                    .update("status", "failed")
                Toast.makeText(this, "Upload failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun listenForDocuments() {
        listenerRegistration = db.collection("documents")
            .orderBy("createdAt")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(this, "Error loading documents: ${e.message}", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                val docs = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(DocumentItem::class.java)
                } ?: emptyList()

                adapter.submitList(docs)
            }
    }
}
