package com.example.doculink

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ChatActivity : AppCompatActivity() {

    private lateinit var input: EditText
    private lateinit var sendBtn: ImageButton   // using btnMic as "send" for now
    private lateinit var chatScroll: ScrollView
    private lateinit var chatContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)

        // Apply insets to the correct root (id = chat_root in your XML)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.chat_root)) { v, insets ->
            val sb = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sb.left, sb.top, sb.right, sb.bottom)
            insets
        }

        input = findViewById(R.id.inputMessage)
        sendBtn = findViewById(R.id.btnMic) // treat as "Send"
        chatScroll = findViewById(R.id.chatScroll)
        chatContainer = findViewById(R.id.chatContainer)

        // Send on button
        sendBtn.setOnClickListener { submitMessage() }

        // Send on keyboard action
        input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE) {
                submitMessage()
                true
            } else false
        }
    }

    private fun submitMessage() {
        val text = input.text?.toString()?.trim().orEmpty()
        if (text.isEmpty()) return

        addMessageBubble(text, isUser = true)
        input.setText("")

        // "typing…" placeholder
        val typingView = addMessageBubble("…", isUser = false, isPlaceholder = true)

        // NOTE: Http.sendQuery returns a String (the summary), not the full object.
        Http.sendQuery(
            query = text,
            onSuccess = { reply: String ->
                runOnUiThread {
                    // Replace placeholder with AI answer (preserve bullets/newlines)
                    replaceBubbleText(typingView, pretty(reply))
                    // (Sources panel removed because Http.kt only returns String)
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

            // readability
            maxWidth = (resources.displayMetrics.widthPixels * 0.85f).toInt()
            setLineSpacing(0f, 1.2f)
            isClickable = true
            isFocusable = true
            setTextIsSelectable(true)
            autoLinkMask = android.text.util.Linkify.WEB_URLS
            movementMethod = LinkMovementMethod.getInstance()

            background = if (isUser) {
                getDrawable(R.drawable.bubble_user) // right-aligned style (e.g., purple bubble)
            } else {
                getDrawable(R.drawable.bubble_bot)  // left-aligned style (e.g., dark gray bubble)
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

    // ---------- helpers for rendering ----------

    private fun pretty(text: String): CharSequence {
        // keep newlines; convert markdown bullets to • ; strip markdown headers
        return text
            .replace(Regex("(?m)^\\s*[-*]\\s+"), "• ")
            .replace(Regex("(?m)^#+\\s*"), "")
    }
}
