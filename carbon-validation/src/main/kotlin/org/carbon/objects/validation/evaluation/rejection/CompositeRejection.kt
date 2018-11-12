package org.carbon.objects.validation.evaluation.rejection

import org.carbon.objects.validation.Logical
import org.carbon.objects.validation.evaluation.Key
import org.carbon.objects.validation.evaluation.KeyModifier
import org.carbon.objects.validation.evaluation.source.CompositionCode
import org.carbon.objects.validation.evaluation.source.ParamList
import org.carbon.objects.validation.evaluation.source.Source

data class CompositeRejection<T : Any>(
        override val original: T,
        private val _logical: Logical,
        private val _rejections: List<Rejection<*>>,
        override val key: Key = Key.Undefined
) : RejectionBase<T>(
        key,
        original,
        Source(
                getCode(_logical),
                ParamList(_rejections),
                getMessage(_logical)
        )
) {
    private companion object {
        fun getCode(logical: Logical): CompositionCode =
                if (logical == Logical.AND) CompositionCode.And
                else CompositionCode.Or

        fun getMessage(logical: Logical): String =
                if (logical == Logical.AND) "Not satisfied with all of conditions"
                else "Not satisfied with any of conditions"
    }

    override fun flattenWithKey(parentModifier: KeyModifier): List<Rejection<*>> =
            if (_rejections.size == 1) _rejections.single().flattenWithKey(parentModifier)
            else listOf(CompositeRejection(
                    original,
                    _logical,
                    _rejections.flatMap { it.flattenWithKey(parentModifier) },
                    parentModifier.modify(key)
            ))

    override fun withNewKey(key: Key): Rejection<T> = CompositeRejection(
            this.original,
            this._logical,
            this._rejections,
            key
    )
}
