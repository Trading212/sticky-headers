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

        val gamesRecyclerItems = listOf(
                "Demon Souls", "Bloodborne", "Overwatch", "Monter Hunter World", "God of War", "WoW", "LoL", "OSU!", "Horizon", "Zelda", "CS"
        ).map { NotDiverseItem(it) }

        val programmingLanguagesItems = listOf(
                "JavaScript", "Swift", "Python", "Java", "C++", "Ruby", "Rust", "Lisp (EW.)", "Haskell", "F#", "SQL", "C#"
        ).map { NotDiverseItem(it) }

        val topSongsItems = listOf(
                "Rainbow Eyes", "Man on the silver mountain", "Blue Morning", "Human", "Try it out", "Sitting on the dock",
                "Alexander Hamilton", "The Trooper", "Nemo", "The Islander", "Jukebox Hero"
        ).map { NotDiverseItem(it) }

        val recyclerItems = mutableListOf<NotDiverseItem>().run {

            add(NotDiverseItem("Top Games", true))
            addAll(gamesRecyclerItems)

            add(NotDiverseItem("Top Programming Languages", true))
            addAll(programmingLanguagesItems)

            add(NotDiverseItem("Top Songs", true))
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

        inner class StickyViewHolder(itemView: View) : TextViewHolder(itemView), StickyHeader<String> {
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
