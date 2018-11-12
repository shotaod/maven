package org.carbon.objects.validation.evaluation

import org.carbon.objects.validation.Describe
import org.carbon.objects.validation.evaluation.rejection.MutableReferenceRejection
import org.carbon.objects.validation.evaluation.rejection.Reference
import org.carbon.objects.validation.evaluation.rejection.Rejection

sealed class Evaluation : Describe {
    object Accepted : Evaluation() {
        override fun describe(i: Int): String = "Accepted"
    }

    class Rejected(private val reference: MutableReferenceRejection) : Evaluation(), Rejection<Reference> by reference {
        // -----------------------------------------------------
        //                                             Computing
        //                                               -------
        private var _cacheFlattenKeyRejections: Map<String, Rejection<*>>? = null
        private val keyedMap: Map<String, Rejection<*>>
            get() {
                _cacheFlattenKeyRejections = _cacheFlattenKeyRejections ?: projectRejections()
                return _cacheFlattenKeyRejections!!
            }

        private fun projectRejections(): Map<String, Rejection<*>> = reference
                .flatten()
                .flatMap(Rejection<*>::flatten)
                .asSequence()
                .groupBy { it.key.qualifiedName }
                .map { e -> e.key to e.value.reduce(Rejection<*>::merge) }
                .toMap()

        // -----------------------------------------------------
        //                                               Iterate
        //                                               -------
        val keys get() = keyedMap.keys
        val entries get() = keyedMap.entries

        operator fun get(keyName: String): Rejection<*>? = keyedMap[keyName]

        companion object {
            fun <T : Any> from(rejection: Rejection<T>): Rejected = Rejected(
                    MutableReferenceRejection().apply {
                        add(rejection)
                    }
            )
        }
    }
}
