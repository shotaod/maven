package org.carbon.objects.validation.evaluation.rejection

import org.carbon.objects.validation.evaluation.HeadModifier
import org.carbon.objects.validation.evaluation.Key
import org.carbon.objects.validation.evaluation.KeyModifier
import org.carbon.objects.validation.evaluation.NoopModifier
import org.carbon.objects.validation.evaluation.source.CompositionCode
import org.carbon.objects.validation.evaluation.source.ParamList
import org.carbon.objects.validation.evaluation.source.Source

object Reference {
    override fun toString(): String = "Reference(no value)"
}

class MutableReferenceRejection(
        override val key: Key = Key.Undefined,
        private val rejections: MutableList<Rejection<*>> = ArrayList()
) : RejectionBase<Reference>(
        key,
        Reference,
        Source(CompositionCode.And, ParamList(rejections))),
        MutableList<Rejection<*>> by rejections {

    override fun withNewKey(key: Key): Rejection<Reference> = MutableReferenceRejection(
            key,
            rejections
    )

    override fun flattenWithKey(parentModifier: KeyModifier): List<Rejection<*>> =
            this.flatMap {
                val thisModifier = if (this.key === Key.Undefined) NoopModifier else HeadModifier(this.key)
                it.flattenWithKey(parentModifier + thisModifier)
            }
}
