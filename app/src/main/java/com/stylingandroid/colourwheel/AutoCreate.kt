package com.stylingandroid.colourwheel

internal class AutoCreate<out T>(
        private val cleanup: (T.() -> Unit)? = null,
        private val creator: () -> T
) {

    private var _backing: T? = null

    val value: T
        get() {
            if (_backing == null) {
                _backing = creator()
                assert(_backing != null)
            }
            @Suppress("UNCHECKED_CAST")
            return _backing as T
        }

    fun clear() {
        _backing?.also {
            cleanup?.invoke(it)
        }
        _backing = null
    }
}
