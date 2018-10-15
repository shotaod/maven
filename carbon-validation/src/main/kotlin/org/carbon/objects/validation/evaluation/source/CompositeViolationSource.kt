package org.carbon.objects.validation.evaluation.source

import org.carbon.objects.validation.evaluation.Logical
import kotlin.reflect.KClass


class CompositeViolationSource<T : Any>(
        _type: KClass<T>,
        compositions: List<ViolationSource<T>>,
        vector: Logical
) : ViolationSource<T>(_type, "Composite".toViolationCode(), listOf(vector) + compositions)
