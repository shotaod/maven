package org.carbon.objects.validation.evaluation.source

class ViolationCode(vararg fragment: String) {
    private val _fragments: List<String> = fragment.toList()
    infix operator fun plus(fragment: String) = ViolationCode(*(_fragments + fragment).toTypedArray())
    infix operator fun plus(fragment: ViolationCode) = ViolationCode(*(_fragments + fragment._fragments).toTypedArray())

    val canonicalName get() = _fragments.joinToString(".")
    override fun toString() = "ViolationCode($canonicalName)"
}

fun String.toViolationCode(): ViolationCode = ViolationCode(*this.split(".").toTypedArray())