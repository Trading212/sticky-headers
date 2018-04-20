package com.trading212.demo

import android.view.View
import com.trading212.demo.item.SimpleStickyTextRecyclerItem
import com.trading212.demo.item.SimpleTextRecyclerItem
import com.trading212.diverserecycleradapter.DiverseRecyclerAdapter
import com.trading212.stickyheader.StickyHeaderDecoration

class DiverseRecyclerAdapterDemoActivity : BaseActivity() {

    private lateinit var stickyHeaderDecoration: StickyHeaderDecoration

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

        stickyHeaderDecoration = StickyHeaderDecoration(recyclerView, adapter)
        recyclerView.addItemDecoration(stickyHeaderDecoration)

        recyclerView.adapter = adapter

        adapter.onItemActionListener = object : DiverseRecyclerAdapter.OnItemActionListener() {
            override fun onItemClicked(v: View, position: Int) {
                adapter.notifyDataSetChanged()
            }
        }

        adapter.notifyDataSetChanged()
    }
}
