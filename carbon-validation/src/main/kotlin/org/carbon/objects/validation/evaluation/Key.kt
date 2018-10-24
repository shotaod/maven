package org.carbon.objects.validation.evaluation

import org.carbon.objects.validation.Describe

open class Key(
        val name: String,
        val index: Int? = null,
        val child: Key? = null
) : Describe {

    companion object {
        val Root = Key("Root")
        val ShouldBeResolved = object : Key("ShouldBeResolved") {
            override val qualifiedName: String
                get() = throw IllegalStateException("this key should be resolved")
        }
    }

    open val qualifiedName: String
        get() = "$name${index?.let { "[$it]" } ?: ""}${child?.let { ".${it.qualifiedName}" } ?: ""}"

    override fun describe(i: Int): String = qualifiedName

    operator fun plus(other: Key): Key = Key(name, index, child?.let { it + other } ?: other)
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

class PrefixModifier(private val parent: Key) : KeyModifier {
    override fun modify(base: Key): Key = parent + base
}