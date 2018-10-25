package org.carbon.objects.validation.evaluation

import org.carbon.objects.validation.Describe
import org.carbon.objects.validation.Logical
import org.carbon.objects.validation.evaluation.rejection.CompositeRejection
import org.carbon.objects.validation.evaluation.source.Source

sealed class Evaluation : Describe {
    object Acceptance : Evaluation() {
        override fun describe(i: Int): String = "Acceptance"
    }

    abstract class Rejection<T : Any>(
            open val key: Key,
            open val original: T,
            open val source: Source
    ) : Evaluation() {
        infix fun modify(keyModifier: KeyModifier): Rejection<T> {
            val newKey = keyModifier.modify(key)
            return newByKey(newKey)
        }

        protected abstract fun newByKey(key: Key): Rejection<T>

        // todo [help] wanna make protected
        // @see https://discuss.kotlinlang.org/t/cannot-access-protected-method-declared-in-super-class/5189/11
        abstract fun flatten(): List<Rejection<*>>

        fun merge(other: Rejection<*>): Rejection<*> =
                @Suppress("UNCHECKED_CAST")
                CompositeRejection(
                        this.original,
                        Logical.AND,
                        listOf(other)
                )

        override fun describe(i: Int): String = """
                |${this::class.simpleName}(
                |${i.render()}key=${key.describe(i.indent())},
                |${i.render()}original=$original,
                |${i.render()}source=${source.describe(i.indent())}
                |${i.render()})
                """.trimMargin()
    }
}
