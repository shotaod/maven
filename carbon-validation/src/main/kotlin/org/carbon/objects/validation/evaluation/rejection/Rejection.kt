package org.carbon.objects.validation.evaluation.rejection

import org.carbon.objects.validation.Describe
import org.carbon.objects.validation.Logical
import org.carbon.objects.validation.evaluation.Key
import org.carbon.objects.validation.evaluation.KeyModifier
import org.carbon.objects.validation.evaluation.NoopModifier
import org.carbon.objects.validation.evaluation.source.Source

interface Rejection<T : Any> : Describe {
    val key: Key
    val original: T
    val source: Source


    fun flatten(): List<Rejection<*>> =
            flattenWithKey(NoopModifier)

    // for internal
    fun flattenWithKey(parentModifier: KeyModifier): List<Rejection<*>>

    infix fun positionedBy(keyModifier: KeyModifier): Rejection<T> =
            withNewKey(keyModifier.modify(key))

    // for internal
    infix fun withNewKey(key: Key): Rejection<T>

    fun merge(other: Rejection<*>): Rejection<*> =
            @Suppress("UNCHECKED_CAST")
            CompositeRejection(
                    this.original,
                    Logical.AND,
                    listOf(this, other)
            )
}

abstract class RejectionBase<T : Any>(
        override val key: Key,
        override val original: T,
        override val source: Source
) : Rejection<T> {
    override fun describe(i: Int): String = """
                |(class: ${this::class.simpleName}) {
                |${i.render()}key=${key.describe(i.indent())},
                |${i.render()}original=$original,
                |${i.render()}source=${source.describe(i.indent())}
                |${i.render()}}
                """.trimMargin()
}