package org.carbon.objects.validation.evaluation.rejection

import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.evaluation.Key
import org.carbon.objects.validation.evaluation.source.Source

data class UnitRejection<T : Any>(
        override var _key: Key,
        override val original: T,
        override val source: Source
) : Evaluation.Rejection<T>(
        _key,
        original,
        source
) {
    override fun flatten(): List<Rejection<*>> = listOf(this)
    override fun merge(other: Rejection<*>): Rejection<*> {
        TODO("function body is not implemented")
    }
}
