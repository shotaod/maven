package org.carbon.objects.validation

/**
 * @author Soda 2018/10/07.
 */
typealias Definition<T> = Assertion.(T) -> Unit

interface Validated<T : Validated<T>> {
    val def: Definition<T>
}
