package com.trading212.stickyheader

import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import java.util.*

/**
 * RecyclerView item decoration that draws sticky headers over the content. The only thing that has to be done is to
 * implement the [StickyHeader] interface in the [RecyclerView.ViewHolder]s that have to become sticky.
 *
 * Only one sticky header at a time is supported for now.
 *
 * **Note** When removal of the [StickyHeaderDecoration] is needed, **always** use [StickyHeaderDecoration.release]
 * to clear touch and scroll listeners needed for the sticky creation to work
 *
 * Although not necessary usage of DiverseRecyclerAdapter simplifies the creation of sticky headers.
 */
class StickyHeaderDecoration : RecyclerView.ItemDecoration() {

    private val stickyHeadersMap: MutableMap<Any, RecyclerView.ViewHolder?> = linkedMapOf()

    // Used for optimisation, creating new instances during draw calls is dangerous
    private val stickyOffsets: MutableMap<Any, Int> = linkedMapOf()

    private val adapterPositionsMap: MutableMap<Any, Int> = linkedMapOf()

    private val onScrollListener: RecyclerView.OnScrollListener = OnScrollListener()

    private val onItemTouchListener: ItemTouchListener = ItemTouchListener()

    private var adapterObserver: AdapterObserver? = null

    private var recyclerView: RecyclerView? = null

    private var adapter: RecyclerView.Adapter<*>? = null

    private var stickyItemViewType = -1

    private var currentStickyId: Any? = null

    private var scrollDeltaY: Int = 0

    private val stickiesStack = Stack<Any>()

    /**
     * Call this when you need to update the sticky headers without notifying the [RecyclerView.Adapter]
     * for changes
     */
    fun updateStickyHeaders() {

        stickyHeadersMap.forEach { entry: Map.Entry<Any, RecyclerView.ViewHolder?> ->
            val viewHolder = entry.value ?: return@forEach
            val adapterPosition = adapterPositionsMap[entry.key] ?: return@forEach

            try {
                updateStickyHeader(viewHolder, adapterPosition)
            } catch (e: ClassCastException) {
                Log.e(javaClass.simpleName, e.message)
            }
        }
    }

    /**
     * Clears the sticky headers, not that they will not be redrawn until they appear on screen again
     */
    fun clear() {
        stickyHeadersMap.clear()
        stickyOffsets.clear()
        adapterPositionsMap.clear()
        stickiesStack.clear()
    }

    override fun onDrawOver(canvas: Canvas, recyclerView: RecyclerView, state: RecyclerView.State) {

        if (this.recyclerView == null) {
            this.recyclerView = recyclerView
        }

        if (adapter !== recyclerView.adapter) {

            if (adapter != null) {
                release()
            }

            adapter = recyclerView.adapter

            registerListeners()
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

                    // New Sticky incoming
                    if (viewTop < STICKY_THRESHOLD || (scrollDeltaY > viewTop && recyclerView.canScrollVertically(1))) {

                        if (stickiesStack.isEmpty() || stickiesStack.peek() != stickyId) {
                            stickiesStack.push(stickyId)
                        }

                        createOrUpdateStickyHeader(view, stickyHeaderViewHolder, recyclerView)
                    } else if (stickiesStack.isNotEmpty() && stickiesStack.peek() == stickyId && viewTop >= 0) {
                        stickiesStack.pop()
                    }

                    currentStickyId = if (stickiesStack.isNotEmpty()) stickiesStack.peek() else null

                    val adapterPosition = (stickyHeaderViewHolder as RecyclerView.ViewHolder).adapterPosition
                    if (adapterPosition != -1) {
                        adapterPositionsMap[stickyId] = adapterPosition
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

    private fun registerListeners() {

        recyclerView?.apply {
            addOnItemTouchListener(onItemTouchListener)

            addOnScrollListener(onScrollListener)
        }

        adapter?.apply {
            if (adapterObserver == null) {
                registerAdapterDataObserver(AdapterObserver().also {
                    adapterObserver = it
                })
            }
        }
    }

    private fun getStickyView(stickyId: Any): View? = stickyHeadersMap[stickyId]?.itemView

    private fun createOrUpdateStickyHeader(stickyView: View, stickyViewHolder: StickyHeader, recyclerView: RecyclerView) {

        val stickyId = stickyViewHolder.stickyId
        val stickyItemViewType = (stickyViewHolder as RecyclerView.ViewHolder).itemViewType

        this.stickyItemViewType = stickyItemViewType

        val adapterPosition = (stickyViewHolder as RecyclerView.ViewHolder).adapterPosition

        stickyHeadersMap.getOrPut(stickyId) {
            val adapter = recyclerView.adapter!!

            val newStickyViewHolder = adapter.onCreateViewHolder(recyclerView, stickyItemViewType)

            adapter.onBindViewHolder(newStickyViewHolder, adapterPosition)

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

            newStickyViewHolder
        }
    }

    private fun updateStickyHeader(viewHolder: RecyclerView.ViewHolder, adapterPosition: Int) {
        val recyclerView = this.recyclerView ?: return
        val adapter = recyclerView.adapter ?: return

        val viewHolderByPosition = recyclerView.findViewHolderForAdapterPosition(adapterPosition)
        if (viewHolderByPosition !is StickyHeader) {
            return
        }

        if (viewHolderByPosition.stickyId == (viewHolder as StickyHeader).stickyId) {
            adapter.onBindViewHolder(viewHolder, adapterPosition)
        }
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
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            scrollDeltaY = dy
        }
    }

    private inner class AdapterObserver : RecyclerView.AdapterDataObserver() {

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {

            val changedRange = positionStart..(positionStart + itemCount)
            stickyHeadersMap.forEach { entry ->
                val viewHolder = entry.value ?: return@forEach
                val adapterPosition = adapterPositionsMap[entry.key] ?: return@forEach

                if (adapterPosition in changedRange) {
                    updateStickyHeader(viewHolder, adapterPosition)
                }
            }

            updateStickyHeaders()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {

            stickyHeadersMap.forEach { entry ->
                val adapterPosition = adapterPositionsMap[entry.key] ?: return@forEach

                if (positionStart <= adapterPosition) {
                    adapterPositionsMap[entry.key] = adapterPosition - itemCount
                }
            }

            updateStickyHeaders()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {

            stickyHeadersMap.forEach { entry ->
                val adapterPosition = adapterPositionsMap[entry.key] ?: return@forEach

                // onItemRangeMoved implementation of RecyclerView doesn't support moves for itemCount > 1
                if (adapterPosition == fromPosition) {
                    adapterPositionsMap[entry.key] = toPosition
                }
            }

            updateStickyHeaders()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {

            stickyHeadersMap.forEach { entry ->
                val adapterPosition = adapterPositionsMap[entry.key] ?: return@forEach

                if (positionStart <= adapterPosition) {
                    adapterPositionsMap[entry.key] = adapterPosition + itemCount
                }
            }

            updateStickyHeaders()
        }

        override fun onChanged() {

            clear()

            updateStickyHeaders()
        }
    }

    companion object {

        private val STICKY_THRESHOLD = dpToPx(2f)

        /**
         * Use this to remove the [StickyHeaderDecoration] from the [recyclerView], also clears [RecyclerView] listeners
         * previously set.
         */
        @JvmStatic
        fun StickyHeaderDecoration.release() {

            recyclerView?.also {
                it.removeItemDecoration(this)

                it.removeOnScrollListener(this.onScrollListener)
                it.removeOnItemTouchListener(this.onItemTouchListener)

                val adapterObserver = this.adapterObserver ?: return

                it.adapter?.unregisterAdapterDataObserver(adapterObserver)
            }
        }
    }
}
