package org.carbon.objects.validation.evaluation.rejection

import org.carbon.objects.validation.evaluation.Evaluation

class RejectionList : MutableList<Evaluation.Rejection<*>> by ArrayList() {
    private var _cacheFlattenKeyRejections: Map<String, Evaluation.Rejection<*>>? = null
    private val keyedMap: Map<String, Evaluation.Rejection<*>>
        get() {
            _cacheFlattenKeyRejections = _cacheFlattenKeyRejections ?: projectRejections()
            return _cacheFlattenKeyRejections!!
        }

    private fun projectRejections(): Map<String, Evaluation.Rejection<*>> = this
            .flatMap(Evaluation.Rejection<*>::flatten)
            .asSequence()
            .groupBy { it.key.qualifiedName }
            .map { e -> e.key to e.value.reduce(Evaluation.Rejection<*>::merge) }
            .toList()
            .toMap()

    val entries get() = keyedMap.entries
    operator fun get(keyName: String): Evaluation.Rejection<*>? = keyedMap[keyName]
}