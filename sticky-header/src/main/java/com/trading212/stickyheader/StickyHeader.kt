package com.trading212.stickyheader

/**
 * Created by petar.marinov on 23.3.2018 г..
 */
interface StickyHeader<T : Comparable<T>> {
    fun stickyId(): T
}