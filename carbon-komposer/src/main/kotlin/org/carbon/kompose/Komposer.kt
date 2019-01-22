package org.carbon.kompose

abstract class Komposable<T> {
    protected var context = Context()
    private lateinit var childKomposer: Komposable<T>
    protected fun callChild() = childKomposer.invoke()

    private fun setChild(child: Komposable<T>): Komposable<T> {
        this.childKomposer = child
        this.context = child.context.merge(this.context)
        return this
    }

    fun kompose(child: Komposable<T>): Komposable<T> {
        setChild(child)
        return this
    }

    abstract fun invoke(): T
}

private class ExpressionKomposer<T>(private val expression: Context.() -> T) : Komposable<T>() {
    override fun invoke(): T = expression(context)
}

private class ArgsExpressionKomposer<T, U>(
    private val expression: Context.(U) -> T,
    private val arg: U) : Komposable<T>() {
    override fun invoke(): T = expression(context, arg)
}

fun <T> kompose(vararg komposers: Komposable<T>, expression: Context.() -> T): T =
    listOf(
        *komposers,
        ExpressionKomposer(expression)
    )
        .reduceRight { rootKomposer, acc -> rootKomposer.kompose(acc) }
        .invoke()

fun <T, U> kompose1(vararg komposers: Komposable<T>, expression: Context.(U) -> T): (U) -> T = { u ->
    listOf(
        *komposers,
        ArgsExpressionKomposer(expression, u)
    )
        .reduceRight { rootKomposer, acc -> rootKomposer.kompose(acc) }
        .invoke()
}

// ______________________________________________________
//
// @ Misc just a hobby ：）
operator fun <T> Komposable<T>.times(komposable: Komposable<T>) = listOf(this, komposable)

operator fun <T> Collection<Komposable<T>>.plus(expression: Context.() -> T): T = kompose(*this.toTypedArray(), expression = expression)
