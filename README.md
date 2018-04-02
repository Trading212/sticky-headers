[ ![Download](https://api.bintray.com/packages/trading-212/maven/sticky-headers/images/download.svg) ](https://bintray.com/trading-212/maven/sticky-headers/_latestVersion)

# Sticky Headers Item Decoration

A simple RecyclerView ItemDecoration that draws sticky headers with minimal ceremony and effort.

*Written in Kotlin, with full Java interoperability*

## Installation

```
implementation 'com.trading212:sticky-headers:0.1.0'
```

## Getting Started

It is only needed for your ViewHolder to implement `StickyHeader` and use a different item type for your Sticky Headers, because they are different items

## Adapter
```kotlin
data class NotDiverseItem(val title: String, val isSticky: Boolean = false)

class NotDiverseRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items: List<NotDiverseItem> = listOf()
    
    // Important part - return a different item type for the sticky item 
    override fun getItemViewType(position: Int) =
        if (items[position].isSticky) {
            STICKY_ITEM_TYPE
        } else {
            TEXT_ITEM_TYPE
        }
        
    companion object {
        val STICKY_ITEM_TYPE = ItemType.STICKY.ordinal

        val TEXT_ITEM_TYPE = ItemType.SIMPLE_TEXT.ordinal
    }
    
    ...
}
```

## ViewHolder
```kotlin
class StickyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), StickyHeader {
    override fun stickyId() = items[adapterPosition].title
}
```

Although it's not needed, the usage of [DiverseRecyclerAdapter](https://github.com/Trading212/DiverseRecyclerAdapter) simplifies setup and separates the logic of each item type in the RecyclerView (in our case that would be a StickyHeader item type and a text item type)

## Tying it all together

Adding the sticky header
```kotlin
recyclerView.addItemDecoration(StickyHeaderDecoration(recyclerView))
```

Removing the sticky header. Due to the need to add additional listeners to the RecyclerView you should **always** use the static helper method, so we can clean up after ourselves
```kotlin
StickyHeaderDecoration.removeFromRecyclerView(recyclerView, stickyHeaderDecoration)
```

Full Example
```kotlin
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

// Adding the sticky header
recyclerView.addItemDecoration(StickyHeaderDecoration(recyclerView))

adapter.notifyDataSetChanged()
```

*For more details, explore and run the **demo** module, as well as the library itself*

## License

Copyright 2018 Trading 212 Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
