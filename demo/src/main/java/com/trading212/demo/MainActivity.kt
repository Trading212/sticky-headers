package com.trading212.demo

import com.trading212.demo.item.ActivityChooserRecyclerItem
import com.trading212.diverserecycleradapter.DiverseRecyclerAdapter

class MainActivity : BaseActivity() {
    override fun fillRecyclerView() {

        val recyclerItems = listOf(
                "Diverse Recycler Adapter Demo" to DiverseRecyclerAdapterDemoActivity::class.java,
                "Generic Recycler Adapter Demo" to GenericRecyclerViewDemoActivity::class.java,
                "Java Diverse Recycler Adapter Demo" to JavaStickyExampleActivity::class.java
        ).map { ActivityChooserRecyclerItem(ActivityChooserRecyclerItem.ActivityInfo(it.first, it.second)) }

        val adapter = DiverseRecyclerAdapter()

        recyclerView.adapter = adapter

        adapter.addItems(recyclerItems, true)
    }
}
