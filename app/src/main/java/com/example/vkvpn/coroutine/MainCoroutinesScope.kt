package com.example.vkvpn.coroutine


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

class MainCoroutinesScope(context: CoroutineContext) : Closeable, CoroutineScope {
    override val coroutineContext: CoroutineContext = context
    override fun close() {
        coroutineContext.cancel()
    }
}