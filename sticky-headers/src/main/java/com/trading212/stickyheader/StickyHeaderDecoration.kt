package com.trading212.stickyheader

import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

/**
 * RecyclerView item decoration that draws sticky headers over the content. The only thing that has to be done is to
 * implement the [StickyHeader] interface in the [RecyclerView.ViewHolder]s that have to become sticky.
 *
 * Only one sticky header at a time is supported for now.
 *
 * **Note** When removal of the [StickyHeaderDecoration] is needed, **always** use [StickyHeaderDecoration.removeFromRecyclerView]
 * to clear touch and scroll listeners needed for the sticky creation to work
 *
 * Although not necessary usage of DiverseRecyclerAdapter simplifies the creation of sticky headers.
 */
class StickyHeaderDecoration(recyclerView: RecyclerView) : RecyclerView.ItemDecoration() {

    private val stickyHeadersMap: MutableMap<Any, View?> = linkedMapOf()

    // Used for optimisation, creating new instances during draw calls are dangerous
    private val stickyOffsets: MutableMap<Any, Int> = hashMapOf()

    private val onScrollListener: RecyclerView.OnScrollListener = OnScrollListener()

    private val onItemTouchListener: ItemTouchListener = ItemTouchListener()

    private var currentStickyId: Any? = null

    private var scrollDeltaY: Int = 0

    init {
        recyclerView.addOnItemTouchListener(onItemTouchListener)

        recyclerView.addOnScrollListener(onScrollListener)
    }

    override fun onDrawOver(canvas: Canvas?, recyclerView: RecyclerView?, state: RecyclerView.State?) {

        if (canvas == null || recyclerView == null) {
            return
        }

        stickyOffsets.clear()

        // Reversed order to try to catch views that can disappear before we reach them
        ((recyclerView.childCount - 1) downTo 0).asSequence()

                // Index to View
                .map { recyclerView.getChildAt(it) }

                // We don't need null views
                .filterNotNull()

                // Pair the view to its ViewHolder and try to cast to StickyHeader
                .map { it to recyclerView.findContainingViewHolder(it) as? StickyHeader }

                .forEach { viewToViewHolderPair ->

                    val view = viewToViewHolderPair.first
                    val stickyHeaderViewHolder = viewToViewHolderPair.second ?: return@forEach

                    val stickyId = stickyHeaderViewHolder.stickyId

                    val viewTop = view.top

                    stickyOffsets[stickyId] = view.top

                    // If view is out of screen or the next frame will probably be
                    if (viewTop < STICKY_THRESHOLD || scrollDeltaY > viewTop) {
                        currentStickyId = stickyId

                        if (!stickyHeadersMap.contains(stickyId)) {
                            createViewForSticky(view, stickyHeaderViewHolder, recyclerView)
                        }
                    } else {
                        if (currentStickyId == stickyId) {
                            currentStickyId = previousStickyId(stickyId)
                        }
                    }
                }

        currentStickyId?.let { currentStickyId ->
            val currentStickyView = getStickyView(currentStickyId) ?: return
            val currentStickyHeight = currentStickyView.height

            val candidateTop = stickyOffsets.asSequence().lastOrNull { it.value in 0..currentStickyHeight }

            if (candidateTop != null) {
                val currentMargin = (currentStickyHeight - candidateTop.value).toFloat()

                if (currentMargin < currentStickyHeight) {
                    canvas.translate(0f, -currentMargin)
                }
            }

            currentStickyView.draw(canvas)
        }
    }

    private fun getStickyView(stickyId: Any): View? = stickyHeadersMap[stickyId]

    private fun createViewForSticky(stickyView: View, stickyViewHolder: StickyHeader, recyclerView: RecyclerView): View? {

        val stickyId = stickyViewHolder.stickyId
        val stickyItemType = (stickyViewHolder as RecyclerView.ViewHolder).itemViewType

        return stickyHeadersMap.getOrPut(stickyId) {
            val adapter = recyclerView.adapter

            val newStickyViewHolder = adapter.onCreateViewHolder(recyclerView, stickyItemType)

            adapter.onBindViewHolder(newStickyViewHolder, (stickyViewHolder as RecyclerView.ViewHolder).adapterPosition)

            val widthSpec = View.MeasureSpec.makeMeasureSpec(recyclerView.measuredWidth, View.MeasureSpec.EXACTLY)
            val heightSpec = View.MeasureSpec.makeMeasureSpec(recyclerView.measuredHeight, View.MeasureSpec.UNSPECIFIED)

            val viewWidth = ViewGroup.getChildMeasureSpec(widthSpec,
                    recyclerView.paddingLeft + recyclerView.paddingRight, stickyView.layoutParams?.width
                    ?: 0)

            val viewHeight = ViewGroup.getChildMeasureSpec(heightSpec,
                    recyclerView.paddingTop + recyclerView.paddingBottom, stickyView.layoutParams?.height
                    ?: 0)

            val newStickyItemView = newStickyViewHolder.itemView
            newStickyItemView.measure(viewWidth, viewHeight)
            newStickyItemView.layout(0, 0, newStickyItemView.measuredWidth, newStickyItemView.measuredHeight)

            newStickyItemView
        }
    }

    private fun previousStickyId(currentKey: Any): Any {

        var previousIterationValue = currentKey

        val keys = stickyHeadersMap.keys

        for (key in keys) {
            if (currentKey == key) {
                break
            }

            previousIterationValue = key
        }

        if (previousIterationValue == keys.last()) {
            previousIterationValue = currentKey
        }

        return previousIterationValue
    }

    private inner class ItemTouchListener : RecyclerView.SimpleOnItemTouchListener() {

        override fun onInterceptTouchEvent(recyclerView: RecyclerView, event: MotionEvent): Boolean {

            if (event.action == MotionEvent.ACTION_MOVE ||
                    (event.action == MotionEvent.ACTION_UP && recyclerView.scrollState == RecyclerView.SCROLL_STATE_DRAGGING)) {
                return false
            }

            currentStickyId?.let {

                val currentStickyViewHeight = getStickyView(it)?.height ?: 0
                val eventY = event.y

                return eventY <= currentStickyViewHeight
            }

            return false
        }
    }

    private inner class OnScrollListener : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            scrollDeltaY = dy
        }
    }

    companion object {

        private val STICKY_THRESHOLD = dpToPx(2f)

        /**
         * Use this to remove the [stickyHeaderDecoration] from the [recyclerView], also clears [RecyclerView] listeners
         * previously set.
         */
        fun removeFromRecyclerView(recyclerView: RecyclerView, stickyHeaderDecoration: StickyHeaderDecoration) {
            recyclerView.removeItemDecoration(stickyHeaderDecoration)

            recyclerView.removeOnScrollListener(stickyHeaderDecoration.onScrollListener)
            recyclerView.removeOnItemTouchListener(stickyHeaderDecoration.onItemTouchListener)
        }
    }
}
