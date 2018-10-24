package org.carbon.objects.validation.evaluation.rejection

import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.evaluation.Key
import org.carbon.objects.validation.evaluation.source.Source

data class UnitRejection<T : Any>(
        override val key: Key,
        override val original: T,
        override val source: Source
) : Evaluation.Rejection<T>(
        key,
        original,
        source
) {
    override fun newByKey(key: Key): Rejection<T> = UnitRejection(
            key,
            this.original,
            this.source
    )

    override fun flatten(): List<Rejection<*>> = listOf(this)
}
