package org.carbon.objects.validation.evaluation

import org.carbon.objects.validation.evaluation.source.Source

data class UnitRejection<T : Any>(
        override var _key: Key,
        override val original: T,
        override val source: Source
) : Evaluation.Rejection<T>(
        _key,
        original,
        source
)
