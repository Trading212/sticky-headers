package com.trading212.stickyheader

import android.content.res.Resources

fun dpToPx(dp: Float): Int {
    return (dp * Resources.getSystem().displayMetrics.density).toInt()
}