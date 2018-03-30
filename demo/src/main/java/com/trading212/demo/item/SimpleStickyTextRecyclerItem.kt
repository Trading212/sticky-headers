package com.trading212.demo.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.trading212.demo.R
import com.trading212.diverserecycleradapter.DiverseRecyclerAdapter
import com.trading212.stickyheader.StickyHeader

class SimpleStickyTextRecyclerItem(title: String) : DiverseRecyclerAdapter.RecyclerItem<String, SimpleStickyTextRecyclerItem.ViewHolder>() {

    companion object {
        private val TYPE = ItemType.STICKY.ordinal
    }

    override val data = title

    override val type = TYPE

    override fun createViewHolder(parent: ViewGroup, inflater: LayoutInflater): ViewHolder = ViewHolder(inflater.inflate(R.layout.item_sticky_text, parent, false))

    class ViewHolder(itemView: View) : DiverseRecyclerAdapter.ViewHolder<String>(itemView), StickyHeader {

        private val textView = findViewById<TextView>(R.id.textView)

        private var data: String? = null

        override fun stickyId() = data ?: ""

        override fun bindTo(data: String?) {

            this.data = data

            textView?.text = data ?: ""
        }
    }
}