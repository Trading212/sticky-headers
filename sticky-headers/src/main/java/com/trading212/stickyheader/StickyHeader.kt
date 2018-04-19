package com.trading212.stickyheader

/**
 * This interface must be implemented by the RecyclerView.ViewHolder.
 *
 * The returned stickyId **must** be unique for the set of StickyHeaders, and should be the same for
 * every call.
 *
 * For example if the StickyHeader represents a date, it is appropriate to return the timestamp or String
 * representation of that date
 *
 */
interface StickyHeader {
    val stickyId: Any
}