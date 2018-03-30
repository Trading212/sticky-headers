package com.trading212.demo

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.trading212.demo.item.ItemType
import com.trading212.stickyheader.StickyHeader
import com.trading212.stickyheader.StickyHeaderDecoration

class GenericRecyclerViewDemoActivity : BaseActivity() {
    override fun fillRecyclerView() {

        val gamesRecyclerItems = generateGamesList().map { RecyclerItem(it) }

        val programmingLanguagesItems = generateProgrammingLanguagesList().map { RecyclerItem(it) }

        val topSongsItems = generateSongsList().map { RecyclerItem(it) }

        val recyclerItems = mutableListOf<RecyclerItem>().run {

            add(RecyclerItem("Games", true))
            addAll(gamesRecyclerItems)

            add(RecyclerItem("Programming Languages", true))
            addAll(programmingLanguagesItems)

            add(RecyclerItem("Songs", true))
            addAll(topSongsItems)

            this
        }

        val adapter = DemoRecyclerAdapter()

        adapter.items = recyclerItems

        recyclerView.adapter = adapter

        recyclerView.addItemDecoration(StickyHeaderDecoration(recyclerView))

        adapter.notifyDataSetChanged()
    }

    data class RecyclerItem(val title: String, val isSticky: Boolean = false)

    class DemoRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var items: List<RecyclerItem> = listOf()

        override fun getItemCount(): Int = items.size

        override fun getItemViewType(position: Int) =
                if (items[position].isSticky) {
                    STICKY_ITEM_TYPE
                } else {
                    TEXT_ITEM_TYPE
                }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val isStickyItem = viewType == STICKY_ITEM_TYPE

            val layoutInflater = LayoutInflater.from(parent.context)

            return if (isStickyItem) {
                StickyViewHolder(layoutInflater.inflate(R.layout.item_sticky_text, parent, false))
            } else {
                TextViewHolder(layoutInflater.inflate(R.layout.item_simple_text, parent, false))
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is TextViewHolder) {

                holder.itemView?.isClickable = true

                holder.textView.text = items[position].title
            }
        }

        inner class StickyViewHolder(itemView: View) : TextViewHolder(itemView), StickyHeader {
            override fun stickyId() = items[adapterPosition].title
        }

        open inner class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView: TextView = itemView.findViewById(R.id.textView)
        }

        companion object {
            val STICKY_ITEM_TYPE = ItemType.STICKY.ordinal

            val TEXT_ITEM_TYPE = ItemType.SIMPLE_TEXT.ordinal
        }
    }
}
