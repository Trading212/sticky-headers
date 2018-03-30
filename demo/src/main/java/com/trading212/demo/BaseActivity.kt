package com.trading212.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import com.trading212.diverserecycleradapter.layoutmanager.DiverseLinearLayoutManager

/**
 * Created by petar.marinov on 29.3.2018 г..
 */
abstract class BaseActivity : AppCompatActivity() {

    protected lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)

        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = DiverseLinearLayoutManager(this)

        fillRecyclerView()
    }

    abstract fun fillRecyclerView()
}