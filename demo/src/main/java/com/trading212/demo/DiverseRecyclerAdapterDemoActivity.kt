package com.trading212.demo

import com.trading212.demo.item.SimpleStickyTextRecyclerItem
import com.trading212.demo.item.SimpleTextRecyclerItem
import com.trading212.diverserecycleradapter.DiverseRecyclerAdapter
import com.trading212.diverserecycleradapter.layoutmanager.DiverseLinearLayoutManager
import com.trading212.stickyheader.StickyHeaderDecoration

class DiverseRecyclerAdapterDemoActivity : BaseActivity() {
    override fun fillRecyclerView() {
        val adapter = DiverseRecyclerAdapter()

        val gamesRecyclerItems = listOf(
                "Demon Souls", "Bloodborne", "Overwatch", "Monter Hunter World", "God of War", "WoW", "LoL", "OSU!", "Horizon", "Zelda", "CS"
        ).map { SimpleTextRecyclerItem(it) }

        val programmingLanguagesItems = listOf(
                "JavaScript", "Swift", "Python", "Java", "C++", "Ruby", "Rust", "Lisp (EW.)", "Haskell", "F#", "SQL", "C#"
        ).map { SimpleTextRecyclerItem(it) }

        val topSongsItems = listOf(
                "Rainbow Eyes", "Man on the silver mountain", "Blue Morning", "Human", "Try it out", "Sitting on the dock",
                "Alexander Hamilton", "The Trooper", "Nemo", "The Islander", "Jukebox Hero"
        ).map { SimpleTextRecyclerItem(it) }

        adapter.addItem(SimpleStickyTextRecyclerItem("Top Games"), false)
        adapter.addItems(gamesRecyclerItems, false)

        adapter.addItem(SimpleStickyTextRecyclerItem("Top Programming Languages"), false)
        adapter.addItems(programmingLanguagesItems, false)

        adapter.addItem(SimpleStickyTextRecyclerItem("Top Songs"), false)
        adapter.addItems(topSongsItems, false)

        val stickyHeaderDecoration = StickyHeaderDecoration(recyclerView)

        recyclerView.addItemDecoration(stickyHeaderDecoration)

        recyclerView.adapter = adapter

        adapter.notifyDataSetChanged()
    }
}
