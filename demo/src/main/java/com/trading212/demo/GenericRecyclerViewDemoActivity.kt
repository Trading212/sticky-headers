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

        val gamesRecyclerItems = generateGamesList().map { NotDiverseItem(it) }

        val programmingLanguagesItems = generateProgrammingLanguagesList().map { NotDiverseItem(it) }

        val topSongsItems = generateSongsList().map { NotDiverseItem(it) }

        val recyclerItems = mutableListOf<NotDiverseItem>().run {

            add(NotDiverseItem("Games", true))
            addAll(gamesRecyclerItems)

            add(NotDiverseItem("Programming Languages", true))
            addAll(programmingLanguagesItems)

            add(NotDiverseItem("Songs", true))
            addAll(topSongsItems)

            this
        }

        val adapter = NotDiverseRecyclerAdapter()

        adapter.items = recyclerItems

        recyclerView.adapter = adapter

        recyclerView.addItemDecoration(StickyHeaderDecoration(recyclerView))

        adapter.notifyDataSetChanged()
    }

    data class NotDiverseItem(val title: String, val isSticky: Boolean = false)

    class NotDiverseRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var items: List<NotDiverseItem> = listOf()

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
