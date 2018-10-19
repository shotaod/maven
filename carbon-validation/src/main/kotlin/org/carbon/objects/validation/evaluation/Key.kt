package org.carbon.objects.validation.evaluation

import org.carbon.objects.validation.Describe

data class Key(
        val name: String,
        val index: Int? = null,
        val child: Key? = null
) : Describe {

    companion object {
        val Root = Key("Root")
        val Unresolved = Key("Undefined")
        fun get(vararg keys: String, index: Int? = null): Key {
            if (keys.isEmpty()) return Key("")
            else if (keys.size == 1) return Key(keys.first(), index)
            val key = keys.first()
            return Key(key, child = get(*keys.slice(1..keys.lastIndex).toTypedArray()))
        }
    }

    infix fun withIndex(i: Int): Key = Key(this.name, i, this.child)

    val qualifiedName: String get() = "$name${index?.let { "[$it]" } ?: ""}${child?.qualifiedName ?: ""}"

    override fun describe(i: Int): String = qualifiedName
}

interface KeyModifier {
    fun modify(base: Key): Key
}

class IndexModifier(private val i: Int) : KeyModifier {
    override fun modify(base: Key): Key = Key(base.name, i, base.child)
}

class NameModifier(private val name: String) : KeyModifier {
    override fun modify(base: Key): Key = Key(name, base.index, base.child)
}
