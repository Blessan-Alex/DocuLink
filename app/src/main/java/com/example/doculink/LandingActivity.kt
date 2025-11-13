package com.example.doculink

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LandingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_landing)

        // Apply insets to your root ConstraintLayout (id = root in your XML)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root)) { v, insets ->
            val sb = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sb.left, sb.top, sb.right, sb.bottom)
            insets
        }

        // Continue with Google – go straight to chat
        findViewById<LinearLayout>(R.id.btnGoogle).setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }

        // Sign up – also proceed to chat (stub)
        findViewById<TextView>(R.id.btnSignUp).setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }

        // Log in – proceed to chat (stub)
        findViewById<TextView>(R.id.btnLogin).setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }
    }
}
