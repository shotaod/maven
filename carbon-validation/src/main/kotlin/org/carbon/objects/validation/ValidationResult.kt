package org.carbon.objects.validation

import org.carbon.objects.validation.evaluation.source.ViolationSource
import kotlin.reflect.KClass

/**
 * @author Soda 2018/10/07.
 */
sealed class ValidationResult

data class ObservanceResult<T>(val data: T) : ValidationResult()
data class ViolationResult(val violations: ViolationList) : ValidationResult()

data class ViolationKey(
        val keys: List<String>,
        val index: Int? = null
)

data class Violation<T : Any>(
        val message: String,
        val key: ViolationKey?,
        val type: KClass<T>,
        val source: ViolationSource<T>
) {
    override fun toString(): String {
        return "Violation(message='$message', key=$key, type=$type, source=$source)"
    }
}

class ViolationList : Iterable<Violation<*>> {
    private val item: MutableList<Violation<*>> = mutableListOf()

    fun add(violation: Violation<*>) = item.add(violation)
    fun isEmpty(): Boolean = item.isEmpty()

    override fun iterator(): Iterator<Violation<*>> = item.iterator()
}