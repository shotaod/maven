package org.carbon.objects.validation.evaluation.source

import kotlin.reflect.KClass

sealed class BasicViolationSource<T : Any>(_type: KClass<T>, _code: ViolationCode, _params: List<*> = emptyList<Any>())
    : ViolationSource<T>(_type, _code, _params) {
    companion object {
        @Suppress("FunctionName")
        inline fun <reified T : Any> Equal(a: T, b: T): BasicViolationSource<T> =
                Equal(T::class, a, b)
    }

    class Equal<T : Any>(_type: KClass<T>, a: T, b: T) : BasicViolationSource<T>(_type, "Equal".toViolationCode(), listOf(a, b))

    sealed class Length<T : Any>(_type: KClass<T>, _code: String, _params: List<Number>)
        : BasicViolationSource<T>(_type, ViolationCode("Length", _code), _params) {
        class Min<T : Any>(_type: KClass<T>, min: Number) : Length<T>(_type, "Min", listOf(min))
        class MinEq<T : Any>(_type: KClass<T>, min: Number) : Length<T>(_type, "MinEq", listOf(min))
        class Max<T : Any>(_type: KClass<T>, max: Number) : Length<T>(_type, "Max", listOf(max))
        class MaxEq<T : Any>(_type: KClass<T>, max: Number) : Length<T>(_type, "MaxEq", listOf(max))

        class Range<T : Any>(_type: KClass<T>, range: IntRange)
            : Length<T>(_type, "MaxEq", listOf(range.start, range.endInclusive))
    }
}
