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
 * **Note** When removal of the [StickyHeaderDecoration] is needed only use [StickyHeaderDecoration.removeFromRecyclerView]
 * to clear touch and scroll listeners needed for the sticky creation to work
 *
 * Although not necessary usage of DiverseRecyclerAdapter simplifies the creation of sticky headers.
 */
class StickyHeaderDecoration(recyclerView: RecyclerView) : RecyclerView.ItemDecoration() {

    private val stickyHeadersMap: MutableMap<Comparable<*>, View?> = linkedMapOf()

    // Used for optimisation, creating new instances during draw calls are dangerous
    private val stickyOffsets: MutableMap<Comparable<*>, Int> = hashMapOf()

    private val onScrollListener: RecyclerView.OnScrollListener = OnScrollListener()

    private val onItemTouchListener: ItemTouchListener = ItemTouchListener()

    private var currentStickyId: Comparable<*>? = null

    private var scrollDeltaY: Int = 0

    private var isScrollTooFast = false

    /**
     * When scrolling/flinging really fast the push animation is not visible at all
     * and is not needed. You can set this to a higher number if you want the push animation to be run.
     * This corresponds to [RecyclerView.OnScrollListener.onScrolled].dy
     *
     * @property fastScrollThreshold
     */
    var fastScrollThreshold = FAST_SCROLL_THRESHOLD

    init {
        recyclerView.addOnItemTouchListener(onItemTouchListener)

        recyclerView.addOnScrollListener(onScrollListener)
    }

    override fun onDrawOver(canvas: Canvas?, recyclerView: RecyclerView?, state: RecyclerView.State?) {

        if (canvas == null || recyclerView == null) {
            return
        }

        fun viewToViewHolderPair(view: View?): Pair<View?, RecyclerView.ViewHolder?> {
            return view to if (view != null) {
                recyclerView.findContainingViewHolder(view)
            } else {
                null
            }
        }

        stickyOffsets.clear()

        // Reversed order to try to catch views that can disappear before we reach them
        ((recyclerView.childCount - 1) downTo 0)
                .asSequence()
                .map { recyclerView.getChildAt(it) }
                .map(::viewToViewHolderPair)
                .filter { it.second is StickyHeader<*> }
                .map { it.first to it.second as StickyHeader<*> }
                .forEach {
                    val view = it.first

                    val stickyId = it.second.stickyId()

                    val viewTop = view?.top ?: -1

                    stickyOffsets[stickyId] = view?.top ?: 0

                    // If view is out of screen or the next frame will probably be
                    if (viewTop < STICKY_THRESHOLD || scrollDeltaY > viewTop) {

                        currentStickyId = stickyId

                        if (!stickyHeadersMap.contains(stickyId)) {
                            createViewForSticky(it, recyclerView)
                        }
                    } else {
                        if (currentStickyId == stickyId) {
                            currentStickyId = stickyHeadersMap.previousKey(stickyId)
                        }
                    }
                }

        currentStickyId?.let { currentStickyId ->
            val currentStickyView = getStickyView(currentStickyId) ?: return
            val currentStickyHeight = currentStickyView.height

            if (!isScrollTooFast) {
                val candidateTop = stickyOffsets.asSequence().lastOrNull { it.value in 1..currentStickyHeight }

                if (candidateTop != null) {
                    val currentMargin = (currentStickyHeight - candidateTop.value).toFloat()

                    if (currentMargin < currentStickyHeight) {
                        canvas.translate(0f, -currentMargin)
                    }
                }
            }

            currentStickyView.draw(canvas)
        }
    }

    private fun getStickyView(stickyId: Comparable<*>): View? = stickyHeadersMap[stickyId]

    private fun createViewForSticky(stickyCandidatePair: Pair<View?, StickyHeader<*>>, recyclerView: RecyclerView): View? {

        val view = stickyCandidatePair.first
        val stickyHeader = stickyCandidatePair.second

        val stickyId = stickyHeader.stickyId()
        val stickyItemType = (stickyHeader as RecyclerView.ViewHolder).itemViewType

        return stickyHeadersMap.getOrPut(stickyId) {
            val adapter = recyclerView.adapter

            val newStickyViewHolder = adapter.onCreateViewHolder(recyclerView, stickyItemType)

            adapter.onBindViewHolder(newStickyViewHolder, (stickyHeader as RecyclerView.ViewHolder).adapterPosition)

            val widthSpec = View.MeasureSpec.makeMeasureSpec(recyclerView.measuredWidth, View.MeasureSpec.EXACTLY)
            val heightSpec = View.MeasureSpec.makeMeasureSpec(recyclerView.measuredHeight, View.MeasureSpec.UNSPECIFIED)

            val viewWidth = ViewGroup.getChildMeasureSpec(widthSpec,
                    recyclerView.paddingLeft + recyclerView.paddingRight, view?.layoutParams?.width ?: 0)

            val viewHeight = ViewGroup.getChildMeasureSpec(heightSpec,
                    recyclerView.paddingTop + recyclerView.paddingBottom, view?.layoutParams?.height ?: 0)

            val newStickyItemView = newStickyViewHolder.itemView
            newStickyItemView.measure(viewWidth, viewHeight)
            newStickyItemView.layout(0, 0, newStickyItemView.measuredWidth, newStickyItemView.measuredHeight)

            newStickyItemView
        }
    }

    private inner class ItemTouchListener : RecyclerView.SimpleOnItemTouchListener() {
        override fun onInterceptTouchEvent(rv: RecyclerView?, e: MotionEvent?): Boolean {

            currentStickyId?.let {

                val currentStickyViewHeight = getStickyView(it)?.height ?: 0
                val eventY = e?.y ?: 0f

                return eventY <= currentStickyViewHeight
            }

            return false
        }
    }

    private inner class OnScrollListener : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            scrollDeltaY = dy
            isScrollTooFast = dy > fastScrollThreshold
        }
    }

    companion object {
        private const val STICKY_THRESHOLD = 5

        private const val FAST_SCROLL_THRESHOLD = 230

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
