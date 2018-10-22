package org.carbon.objects.validation.evaluation.rejection

import org.carbon.objects.validation.Logical
import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.evaluation.Key
import org.carbon.objects.validation.evaluation.KeyReplacer
import org.carbon.objects.validation.evaluation.source.CompositionCode
import org.carbon.objects.validation.evaluation.source.Param
import org.carbon.objects.validation.evaluation.source.Source

data class CompositeRejection<T : Any>(
        override var _key: Key,
        override val original: T,
        private val logical: Logical,
        private val _rejections: List<Rejection<*>>
) : Evaluation.Rejection<T>(
        _key,
        original,
        Source(
                getCode(logical),
                Param(_rejections),
                getMessage(logical)
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

    override fun flatten(): List<Rejection<*>> =
            if (_rejections.size == 1) listOf(_rejections.single() modify KeyReplacer(_key))
            else listOf(this)

    override fun merge(other: Rejection<*>): Rejection<*> {
        TODO("function body is not implemented")
    }
}