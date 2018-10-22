package org.carbon.objects.validation.evaluation.rejection

import org.carbon.objects.validation.evaluation.Evaluation.Rejection
import org.carbon.objects.validation.evaluation.Key
import org.carbon.objects.validation.evaluation.PrefixModifier
import org.carbon.objects.validation.evaluation.source.CompositionCode
import org.carbon.objects.validation.evaluation.source.DelegateParam
import org.carbon.objects.validation.evaluation.source.Source
import java.util.*

object Root {
    override fun toString(): String = "ROOT_VALUE"
}

open class RootRejection(
        private val _rejections: RejectionList = RejectionList()
) : Rejection<Root>(
        Key.Root,
        Root,
        Source(CompositionCode.And, DelegateParam(_rejections))
) {
    fun isValid(): Boolean = _rejections.isEmpty()
    fun addRejection(rejection: Rejection<*>) {
        _rejections.add(rejection)
    }

    operator fun get(key: String): Rejection<*>? = _rejections[key]

    override fun describe(i: Int): String = _rejections.all.entries
            .joinToString(separator = "\n", prefix = "Rejection(\n${i.render()}", postfix = ")")
            { "${i.indent().render()}- [${it.key}]: ${it.value.describe(i.indent().indent().indent())}" }

    override fun flatten(): List<Rejection<*>> =
            if (key === Key.Root) _rejections
            else _rejections.flatMap { (it modify PrefixModifier(key.name)).flatten() }

    override fun merge(other: Rejection<*>): Rejection<*> = throw UnsupportedOperationException()
}

class RejectionList : MutableList<Rejection<*>> by ArrayList() {
    private var _cacheFlattenKeyRejections: Map<String, Rejection<*>>? = null
    val all: Map<String, Rejection<*>>
        get() {
            _cacheFlattenKeyRejections = _cacheFlattenKeyRejections ?: projectRejections()
            return _cacheFlattenKeyRejections!!
        }

    private fun projectRejections(): Map<String, Rejection<*>> = this
            .flatMap(Rejection<*>::flatten)
            .asSequence()
            .groupBy { it.key.qualifiedName }
            .map { e -> e.key to e.value.reduce(Rejection<*>::merge) }
            .toList()
            .toMap()

    operator fun get(key: String): Rejection<*>? = all[key]
}