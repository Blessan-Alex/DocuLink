package com.example.doculink

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DocumentAdapter(
    private var items: List<DocumentItem>,
    private val onClick: (DocumentItem) -> Unit
) : RecyclerView.Adapter<DocumentAdapter.DocViewHolder>() {

    inner class DocViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.txtDocTitle)
        val tag: TextView = itemView.findViewById(R.id.txtDocTag)
        val desc: TextView = itemView.findViewById(R.id.txtDocDescription)
        val meta: TextView = itemView.findViewById(R.id.txtDocMeta)
        val status: TextView = itemView.findViewById(R.id.txtDocStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_document_card, parent, false)
        return DocViewHolder(view)
    }

    override fun onBindViewHolder(holder: DocViewHolder, position: Int) {
        val doc = items[position]
        holder.title.text = doc.title
        holder.tag.text = doc.tag
        holder.desc.text = doc.description
        holder.meta.text = "${doc.date} • ${doc.size}"

        holder.status.text = when (doc.status) {
            "embedding_in_progress" -> "Embedding in progress (~10 minutes)…"
            "ready" -> "Embedded"
            "failed" -> "Embedded"
            else -> ""
        }

        holder.itemView.setOnClickListener { onClick(doc) }
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<DocumentItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
