package org.carbon.composer

import kotlin.reflect.KClass

class Context {
    private val internalContext: MutableMap<KClass<*>, Any> = mutableMapOf()

    class ContextReader(private val internalContext: MutableMap<KClass<*>, Any>) {
        operator fun <K : Any> get(k: KClass<K>): K = internalContext[k]
            ?.let {
                @Suppress("UNCHECKED_CAST")
                it as? K
            }
            ?: throw IllegalStateException("type mismatch")
    }

    fun set(value: Any) {
        internalContext[value::class] = value
    }

    fun <T : Any> setAs(value: T, kClass: KClass<T>) {
        internalContext[kClass] = value
    }

    fun merge(context: Context): Context {
        internalContext.putAll(context.internalContext)
        return this
    }

    val context: ContextReader get() = ContextReader(internalContext)
}