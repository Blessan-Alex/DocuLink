package com.example.doculink

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.method.LinkMovementMethod
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout

class ChatActivity : AppCompatActivity() {

    private val REQ_CODE_SPEECH = 1001

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var input: EditText
    private lateinit var sendBtn: ImageButton
    private lateinit var chatScroll: ScrollView
    private lateinit var chatContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)

        drawerLayout = findViewById(R.id.drawerLayout)

        // Apply insets to correct root (chat_root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.chat_root)) { v, insets ->
            val sb = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sb.left, sb.top, sb.right, sb.bottom)
            insets
        }

        input = findViewById(R.id.inputMessage)
        sendBtn = findViewById(R.id.btnMic)              // right button = SEND
        chatScroll = findViewById(R.id.chatScroll)
        chatContainer = findViewById(R.id.chatContainer)

        val micBtn: ImageButton = findViewById(R.id.btnAdd)   // left button = MIC

        findViewById<ImageButton>(R.id.btnMenu).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        findViewById<TextView>(R.id.navDashboard).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, MetroDashboardActivity::class.java))
        }
        findViewById<TextView>(R.id.navDocuments).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, EmbeddedDocumentsActivity::class.java))
        }

        // Send on button click (right icon)
        sendBtn.setOnClickListener { submitMessage() }

        // Mic / voice input on left icon
        micBtn.setOnClickListener { startVoiceInput() }

        // Send on keyboard action
        input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE) {
                submitMessage()
                true
            } else false
        }

        onBackPressedDispatcher.addCallback(this) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                finish()
            }
        }
    }

    private fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            // Use whatever locale you prefer; en-IN is fine for India
            // putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN,ml-IN");


            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your query for DocuLink")
        }

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this,
                "Speech input not supported on this device",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQ_CODE_SPEECH && resultCode == RESULT_OK && data != null) {
            val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = result?.firstOrNull().orEmpty()

            if (spokenText.isNotEmpty()) {
                // Put recognized text into the input box; user can edit or just hit send
                input.setText(spokenText)
                input.setSelection(spokenText.length)
            }
        }
    }

    private fun submitMessage() {
        val text = input.text?.toString()?.trim().orEmpty()
        if (text.isEmpty()) return

        addMessageBubble(text, isUser = true)
        input.setText("")

        // Typing placeholder
        val typingView = addMessageBubble("…", isUser = false, isPlaceholder = true)

        Http.sendQuery(
            query = text,
            onSuccess = { reply: String ->
                runOnUiThread {
                    replaceBubbleText(typingView, pretty(reply))
                    scrollToBottom()
                }
            },
            onError = { err ->
                runOnUiThread {
                    replaceBubbleText(typingView, "Error: $err")
                    scrollToBottom()
                }
            }
        )
    }

    private fun addMessageBubble(
        message: String,
        isUser: Boolean,
        isPlaceholder: Boolean = false
    ): TextView {
        val tv = TextView(this).apply {
            text = message
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 16f
            setPadding(28, 20, 28, 20)

            maxWidth = (resources.displayMetrics.widthPixels * 0.85f).toInt()
            setLineSpacing(0f, 1.2f)
            isClickable = true
            isFocusable = true
            setTextIsSelectable(true)
            autoLinkMask = android.text.util.Linkify.WEB_URLS
            movementMethod = LinkMovementMethod.getInstance()

            background = if (isUser) {
                getDrawable(R.drawable.bubble_user)
            } else {
                getDrawable(R.drawable.bubble_bot)
            }
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = 12
            bottomMargin = 4
            marginStart = if (isUser) 56 else 12
            marginEnd = if (isUser) 12 else 56
            gravity = if (isUser) android.view.Gravity.END else android.view.Gravity.START
        }

        chatContainer.addView(tv, params)
        scrollToBottom()
        return tv
    }

    private fun replaceBubbleText(bubble: TextView, newText: CharSequence) {
        bubble.text = newText
    }

    private fun scrollToBottom() {
        chatScroll.post { chatScroll.fullScroll(ScrollView.FOCUS_DOWN) }
    }

    // ---------- helpers ----------
    private fun pretty(text: String): CharSequence {
        return text
            .replace(Regex("(?m)^\\s*[-*]\\s+"), "• ")
            .replace(Regex("(?m)^#+\\s*"), "")
    }
}
