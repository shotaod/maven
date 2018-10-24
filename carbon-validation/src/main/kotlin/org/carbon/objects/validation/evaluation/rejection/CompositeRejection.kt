package org.carbon.objects.validation.evaluation.rejection

import org.carbon.objects.validation.Logical
import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.evaluation.Key
import org.carbon.objects.validation.evaluation.KeyReplacer
import org.carbon.objects.validation.evaluation.source.CompositionCode
import org.carbon.objects.validation.evaluation.source.Param
import org.carbon.objects.validation.evaluation.source.Source

data class CompositeRejection<T : Any>(
        override val original: T,
        private val _logical: Logical,
        private val _rejections: List<Rejection<*>>
) : Evaluation.Rejection<T>(
        getKey(_rejections),
        original,
        Source(
                getCode(_logical),
                Param(_rejections),
                getMessage(_logical)
        )
) {
    private companion object {
        fun getKey(rejections: List<Rejection<*>>) = rejections.first().key
        fun getCode(logical: Logical): CompositionCode =
                if (logical == Logical.AND) CompositionCode.And
                else CompositionCode.Or

        fun getMessage(logical: Logical): String =
                if (logical == Logical.AND) "Not satisfied with all of conditions"
                else "Not satisfied with any of conditions"
    }

    override fun newByKey(key: Key): Rejection<T> = CompositeRejection(
            this.original,
            this._logical,
            this._rejections.map { it modify KeyReplacer(key) }
    )

    override fun flatten(): List<Rejection<*>> =
            if (_rejections.size == 1) listOf(_rejections.single() modify KeyReplacer(key))
            else listOf(this)
}