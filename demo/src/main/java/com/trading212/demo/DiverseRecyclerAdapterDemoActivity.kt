package com.trading212.demo

import android.view.View
import com.trading212.demo.item.SimpleStickyTextRecyclerItem
import com.trading212.demo.item.SimpleTextRecyclerItem
import com.trading212.diverserecycleradapter.DiverseRecyclerAdapter
import com.trading212.stickyheader.StickyHeaderDecoration

class DiverseRecyclerAdapterDemoActivity : BaseActivity() {

    private lateinit var stickyHeaderDecoration: StickyHeaderDecoration

    private var stickyIdsCounter = 0

    override fun fillRecyclerView() {
        val adapter = DiverseRecyclerAdapter()

        val gamesRecyclerItems = generateGamesList().map { SimpleTextRecyclerItem(it) }

        val programmingLanguagesItems = generateProgrammingLanguagesList().map { SimpleTextRecyclerItem(it) }

        val topSongsItems = generateSongsList().map { SimpleTextRecyclerItem(it) }

        adapter.addItem(SimpleStickyTextRecyclerItem(SimpleStickyTextRecyclerItem.StickyData("Games", ++stickyIdsCounter)), false)
        adapter.addItems(gamesRecyclerItems, false)

        adapter.addItem(SimpleStickyTextRecyclerItem(SimpleStickyTextRecyclerItem.StickyData("Programming Languages", ++stickyIdsCounter)), false)
        adapter.addItems(programmingLanguagesItems, false)

        adapter.addItem(SimpleStickyTextRecyclerItem(SimpleStickyTextRecyclerItem.StickyData("Songs", ++stickyIdsCounter)), false)
        adapter.addItems(topSongsItems, false)

        stickyHeaderDecoration = StickyHeaderDecoration()
        recyclerView.addItemDecoration(stickyHeaderDecoration)

        recyclerView.adapter = adapter

        adapter.onItemActionListener = object : DiverseRecyclerAdapter.OnItemActionListener() {
            override fun onItemClicked(v: View, position: Int) {

                adapter.insertItem(0,
                        SimpleStickyTextRecyclerItem(SimpleStickyTextRecyclerItem.StickyData("Item${System.currentTimeMillis()}", ++stickyIdsCounter)))
                adapter.insertItem(0, SimpleTextRecyclerItem("Item${System.currentTimeMillis()}"))
            }

            override fun onItemLongClicked(v: View, position: Int): Boolean {

                adapter.removeItem(1)

                return super.onItemLongClicked(v, position)
            }
        }

        adapter.notifyDataSetChanged()
    }
}
