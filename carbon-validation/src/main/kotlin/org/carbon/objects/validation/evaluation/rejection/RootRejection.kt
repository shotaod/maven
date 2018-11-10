package org.carbon.objects.validation.evaluation.rejection

import org.carbon.objects.validation.evaluation.Evaluation.Rejection
import org.carbon.objects.validation.evaluation.Key
import org.carbon.objects.validation.evaluation.PrefixModifier
import org.carbon.objects.validation.evaluation.source.CompositionCode
import org.carbon.objects.validation.evaluation.source.ParamList
import org.carbon.objects.validation.evaluation.source.Source

object Root

open class RootRejection(
        override val key: Key = Key.Root,
        private val _rejections: RejectionList = RejectionList()
) : Rejection<Root>(
        key,
        Root,
        Source(CompositionCode.And, ParamList(_rejections))
) {
    fun isValid(): Boolean = _rejections.isEmpty()
    fun addRejection(rejection: Rejection<*>) {
        _rejections.add(rejection)
    }

    operator fun get(key: String): Rejection<*>? = _rejections[key]

    fun entries() = _rejections.entries

    override fun newByKey(key: Key): Rejection<Root> = RootRejection(
            key,
            this._rejections
    )

    override fun describe(i: Int): String = _rejections.entries
            .joinToString(separator = "\n", prefix = "Rejection(\n", postfix = ")")
            { "${i.render()}- [${it.key}]: ${it.value.describe(i.indent())}" }

    override fun flatten(): List<Rejection<*>> =
            if (key === Key.Root) throw IllegalStateException("this method[flatten] is for internal use. Not call directly")
            else _rejections.flatMap { (it modify PrefixModifier(key)).flatten() }
}
