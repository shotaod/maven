package org.carbon.objects.validation.evaluation

import org.carbon.objects.validation.Describe
import org.carbon.objects.validation.evaluation.source.Code
import org.carbon.objects.validation.evaluation.source.DelegateParam
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
                |${i.space()}key=${key.describe(i.indent())},
                |${i.space()}original=$original,
                |${i.space()}source=${source.describe(i.indent())}
                |${i.space()})
                """.trimMargin()
    }

    object Root {
        override fun toString(): String = "ROOT_VALUE"
    }

    class RootRejection(
            private val _rejections: MutableList<Rejection<*>> = mutableListOf()
    ) : Rejection<Root>(
            Key.Root,
            Root,
            Source(Code("Root"), DelegateParam(_rejections))
    ) {
        fun isValid(): Boolean = _rejections.isEmpty()
        fun addRejection(rejection: Rejection<*>) {
            _rejections.add(rejection)
        }
        val rejections get() = _rejections
    }
}
