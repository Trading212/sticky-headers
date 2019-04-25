package com.trading212.demo.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.trading212.demo.R
import com.trading212.diverserecycleradapter.DiverseRecyclerAdapter
import com.trading212.stickyheader.StickyHeader

class SimpleStickyTextRecyclerItem(stickyData: StickyData) :
        DiverseRecyclerAdapter.RecyclerItem<SimpleStickyTextRecyclerItem.StickyData, SimpleStickyTextRecyclerItem.ViewHolder>() {

    override val data = stickyData

    override val type = TYPE

    override fun createViewHolder(parent: ViewGroup, inflater: LayoutInflater): ViewHolder = ViewHolder(
            inflater.inflate(R.layout.item_sticky_text, parent, false))

    class ViewHolder(itemView: View) : DiverseRecyclerAdapter.ViewHolder<StickyData>(itemView), StickyHeader {

        override val stickyId
            get() = data.headerId

        private val textView = findViewById<TextView>(R.id.textView)

        private lateinit var data: StickyData

        override fun bindTo(data: StickyData?) {

            data ?: return

            this.data = data

            textView?.text = data.text
        }
    }

    companion object {
        private val TYPE = ItemType.STICKY.ordinal
    }

    class StickyData(var text: String, val headerId: Int)
}