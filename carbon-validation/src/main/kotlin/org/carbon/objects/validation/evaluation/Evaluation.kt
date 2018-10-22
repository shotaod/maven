package org.carbon.objects.validation.evaluation

import org.carbon.objects.validation.Describe
import org.carbon.objects.validation.evaluation.source.Source

sealed class Evaluation : Describe {
    object Acceptance : Evaluation() {
        override fun describe(i: Int): String = "Acceptance"
    }

    abstract class Rejection<T : Any>(
            protected open var _key: Key,
            open val original: T,
            open val source: Source
    ) : Evaluation() {
        infix fun modify(keyModifier: KeyModifier): Rejection<T> {
            this._key = keyModifier.modify(key)
            return this
        }

        val key: Key get() = _key

        override fun describe(i: Int): String = """
                |${this::class.simpleName}(
                |${i.render()}key=${key.describe(i.indent())},
                |${i.render()}original=$original,
                |${i.render()}source=${source.describe(i.indent())}
                |${i.render()})
                """.trimMargin()

        // todo [help] wanna make protected
        // @see https://discuss.kotlinlang.org/t/cannot-access-protected-method-declared-in-super-class/5189/11
        abstract fun flatten(): List<Rejection<*>>

        abstract fun merge(other: Rejection<*>): Rejection<*>
    }
}
