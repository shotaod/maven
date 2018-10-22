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
    }

    val qualifiedName: String
        get() = "$name${index?.let { "[$it]" } ?: ""}${child?.let { ".${it.qualifiedName}" } ?: ""}"

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

class KeyReplacer(private val key: Key) : KeyModifier {
    override fun modify(base: Key): Key = key
}

class PrefixModifier(private val prefix: String) : KeyModifier {
    override fun modify(base: Key): Key = Key(prefix, null, child = base)
}