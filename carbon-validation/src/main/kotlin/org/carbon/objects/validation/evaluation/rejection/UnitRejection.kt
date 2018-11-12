package org.carbon.objects.validation.evaluation.rejection

import org.carbon.objects.validation.evaluation.Key
import org.carbon.objects.validation.evaluation.KeyModifier
import org.carbon.objects.validation.evaluation.source.Source

data class UnitRejection<T : Any>(
        override val key: Key,
        override val original: T,
        override val source: Source
) : RejectionBase<T>(
        key,
        original,
        source
) {
    override fun withNewKey(key: Key): Rejection<T> = UnitRejection(
            key,
            this.original,
            this.source
    )

    override fun flattenWithKey(parentModifier: KeyModifier) = listOf(
            UnitRejection(
                    parentModifier.modify(key),
                    original,
                    source
            )
    )
}
