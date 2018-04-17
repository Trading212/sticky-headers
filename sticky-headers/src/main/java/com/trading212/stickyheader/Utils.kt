package com.trading212.stickyheader

/**
 * @return the previous key for the given [currentKey] or [currentKey] if there is no previous key
 */
fun MutableMap<Any, *>.previousKey(currentKey: Any): Any {

    var previousIterationValue = currentKey

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