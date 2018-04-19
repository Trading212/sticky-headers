package com.trading212.demo

import com.trading212.demo.item.SimpleStickyTextRecyclerItem
import com.trading212.demo.item.SimpleTextRecyclerItem
import com.trading212.diverserecycleradapter.DiverseRecyclerAdapter
import com.trading212.stickyheader.StickyHeaderDecoration

class DiverseRecyclerAdapterDemoActivity : BaseActivity() {

    override fun fillRecyclerView() {
        val adapter = DiverseRecyclerAdapter()

        val gamesRecyclerItems = generateGamesList().map { SimpleTextRecyclerItem(it) }

        val programmingLanguagesItems = generateProgrammingLanguagesList().map { SimpleTextRecyclerItem(it) }

        val topSongsItems = generateSongsList().map { SimpleTextRecyclerItem(it) }

        adapter.addItem(SimpleStickyTextRecyclerItem("Games"), false)
        adapter.addItems(gamesRecyclerItems, false)

        adapter.addItem(SimpleStickyTextRecyclerItem("Programming Languages"), false)
        adapter.addItems(programmingLanguagesItems, false)

        adapter.addItem(SimpleStickyTextRecyclerItem("Songs"), false)
        adapter.addItems(topSongsItems, false)

        recyclerView.addItemDecoration(StickyHeaderDecoration(recyclerView))

        recyclerView.adapter = adapter

        adapter.notifyDataSetChanged()
    }
}
