package com.trading212.demo;

import com.trading212.demo.item.SimpleStickyTextRecyclerItem;
import com.trading212.demo.item.SimpleTextRecyclerItem;
import com.trading212.diverserecycleradapter.DiverseRecyclerAdapter;
import com.trading212.diverserecycleradapter.DiverseRecyclerAdapter.RecyclerItem;
import com.trading212.diverserecycleradapter.DiverseRecyclerAdapter.ViewHolder;
import com.trading212.stickyheader.StickyHeaderDecoration;

import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;

public class JavaStickyExampleActivity extends BaseActivity {
    @Override
    public void fillRecyclerView() {
        DiverseRecyclerAdapter adapter = new DiverseRecyclerAdapter();

        // Games
        adapter.addItem(new SimpleStickyTextRecyclerItem(new SimpleStickyTextRecyclerItem.StickyData("Games", 1)), false);
        adapter.addItems(CollectionsKt.map(generateGamesList(), new Function1<String, RecyclerItem<?, ? extends ViewHolder<?>>>() {
            @Override
            public RecyclerItem<?, ? extends ViewHolder<?>> invoke(String game) {
                return new SimpleTextRecyclerItem(game);
            }
        }), false);

        // Programming Languages
        adapter.addItem(new SimpleStickyTextRecyclerItem(new SimpleStickyTextRecyclerItem.StickyData("Programming Languages", 2)), false);
        adapter.addItems(CollectionsKt.map(generateProgrammingLanguagesList(), new Function1<String, RecyclerItem<?, ? extends ViewHolder<?>>>() {
            @Override
            public RecyclerItem<?, ? extends ViewHolder<?>> invoke(String language) {
                return new SimpleTextRecyclerItem(language);
            }
        }), false);

        // Songs
        adapter.addItem(new SimpleStickyTextRecyclerItem(new SimpleStickyTextRecyclerItem.StickyData("Songs", 3)), false);
        adapter.addItems(CollectionsKt.map(generateSongsList(), new Function1<String, RecyclerItem<?, ? extends ViewHolder<?>>>() {
            @Override
            public RecyclerItem<?, ? extends ViewHolder<?>> invoke(String song) {
                return new SimpleTextRecyclerItem(song);
            }
        }), false);

        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new StickyHeaderDecoration());

        adapter.notifyDataSetChanged();
    }
}
