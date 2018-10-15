package org.carbon.objects.validation.evaluation

import org.carbon.objects.validation.evaluation.source.ViolationSource
import kotlin.reflect.KClass

sealed class Evaluation {
    object Accept : Evaluation() {
        override fun toString(): String {
            return "Accept()"
        }
    }

    data class Reject<T : Any>(
            val type: KClass<T>,
            val source: ViolationSource<T>,
            val message: String,
            val original: T
    ) : Evaluation() {
        override fun toString(): String {
            return "Reject(type=$type, source=$source, message='$message', original=$original)"
        }
    }
}

inline fun <reified T : Any> T.reject(source: ViolationSource<T>, message: String) =
        Evaluation.Reject(T::class, source, message, this)
