package com.example.doculink

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DocumentAdapter(
    private val items: List<DocumentItem>,
    private val onItemClick: (DocumentItem) -> Unit
) : RecyclerView.Adapter<DocumentAdapter.DocViewHolder>() {

    inner class DocViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.txtDocTitle)
        val tag: TextView = view.findViewById(R.id.txtDocTag)
        val desc: TextView = view.findViewById(R.id.txtDocDescription)
        val meta: TextView = view.findViewById(R.id.txtDocMeta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_document_card, parent, false)
        return DocViewHolder(v)
    }

    override fun onBindViewHolder(holder: DocViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.tag.text = item.tag
        holder.desc.text = item.description
        holder.meta.text = "${item.date} • ${item.size}"

        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = items.size
}



