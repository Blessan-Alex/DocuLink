package com.example.doculink

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MetroDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metro_dashboard)

        findViewById<Button>(R.id.btnOpenDocuments).setOnClickListener {
            startActivity(Intent(this, EmbeddedDocumentsActivity::class.java))
        }
    }
}



