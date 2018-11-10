package org.carbon.objects.validation.evaluation.source

import org.carbon.objects.validation.Describe

/**
 * @author Soda 2018/10/08.
 */

open class Code(
        private val name: String,
        protected open val child: Code? = null
) : Describe {
    val canonicalName: String get() = "$name${child?.let { ".${it.name}" } ?: ""}"
    override fun describe(i: Int): String = canonicalName
}

sealed class BasicCode(override val child: Code) : Code("Basic", child) {
    object Equal : BasicCode(Code("Equal"))
    object NotEqual : BasicCode(Code("NotEqual"))
}

sealed class CompositionCode(override val child: Code) : Code("Composition", child) {
    object And : CompositionCode(Code("And"))
    object Or : CompositionCode(Code("Or"))
}

sealed class LengthCode(override val child: Code) : Code("Length", child) {
    object Equal : LengthCode(Code("Equal"))
    object Min : LengthCode(Code("Min"))
    object Max : LengthCode(Code("Max"))
}

sealed class IncludeCode(override val child: Code) : Code("Include", child) {
    object Any : IncludeCode(Code("Any"))
    object All : IncludeCode(Code("All"))
}

abstract class StringCode(override val child: Code) : Code("String", child) {
    object Regex : StringCode(Code("Regex"))
    object Email : StringCode(Code("Email"))
    object URL : StringCode(Code("URL"))
    object Contain : StringCode(Code("Contain"))
}

abstract class NumberCode(override val child: Code) : Code("Number", child) {
    object Natural : NumberCode(Code("Natural"))
}
