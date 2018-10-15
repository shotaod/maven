package org.carbon.objects.validation.evaluation.source

import org.carbon.objects.validation.evaluation.source.BasicViolationSource.Length.Max
import org.carbon.objects.validation.evaluation.source.BasicViolationSource.Length.MaxEq
import org.carbon.objects.validation.evaluation.source.BasicViolationSource.Length.Min
import org.carbon.objects.validation.evaluation.source.BasicViolationSource.Length.MinEq
import org.carbon.objects.validation.evaluation.source.BasicViolationSource.Length.Range
import kotlin.reflect.KClass

/**
 * @author Soda 2018/10/08.
 */

@Suppress("MemberVisibilityCanBePrivate")
abstract class ViolationSource<T : Any>(
        val type: KClass<T>,
        val code: ViolationCode,
        val params: List<*> = emptyList<Any>()) {
    override fun toString(): String {
        return "ViolationSource(type=${type.simpleName}, code='$code', params=$params)"
    }
}

// -----------------------------------------------------
//                                               String
//                                               -------
abstract class StringViolationSource(_code: ViolationCode, _params: List<*> = emptyList<Any>())
    : ViolationSource<String>(String::class, _code, _params) {
    object Email : StringViolationSource("Email".toViolationCode())
    object URL : StringViolationSource("URL".toViolationCode())

    companion object {
        fun min(min: Int) = Min(String::class, min)
        fun minEq(min: Int) = MinEq(String::class, min)
        fun max(min: Int) = Max(String::class, min)
        fun maxEq(min: Int) = MaxEq(String::class, min)
        fun range(range: IntRange) = Range(String::class, range)
    }
}

// -----------------------------------------------------
//                                               Number
//                                               -------
sealed class NumberViolationSource(_code: String, _params: List<*> = emptyList<Any>())
    : ViolationSource<Number>(Number::class, ViolationCode("Number") + _code, _params) {

    companion object {
        fun min(min: Number) = Min(Number::class, min)
        fun minEq(min: Number) = MinEq(Number::class, min)
        fun max(min: Number) = Max(Number::class, min)
        fun maxEq(min: Number) = MaxEq(Number::class, min)
        fun range(range: IntRange) = Range(Number::class, range)
    }
}
