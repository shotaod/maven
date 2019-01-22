package org.carbon.composer

abstract class Composable<T> {
    protected var context = Context()
    private lateinit var childComposer: Composable<T>
    protected fun callChild() = childComposer.invoke()

    private fun setChild(child: Composable<T>): Composable<T> {
        this.childComposer = child
        this.context = child.context.merge(this.context)
        return this
    }

    fun compose(child: Composable<T>): Composable<T> {
        setChild(child)
        return this
    }

    abstract fun invoke(): T
}

private class ExpressionComposer<T>(private val expression: Context.() -> T) : Composable<T>() {
    override fun invoke(): T = expression(context)
}

private class ArgsExpressionComposer<T, U>(
    private val expression: Context.(U) -> T,
    private val arg: U) : Composable<T>() {
    override fun invoke(): T = expression(context, arg)
}

fun <T> kompose(vararg composers: Composable<T>, expression: Context.() -> T): T =
    listOf(
        *composers,
        ExpressionComposer(expression)
    )
        .reduceRight { rootComposer, acc -> rootComposer.compose(acc) }
        .invoke()

fun <T, U> kompose1(vararg composers: Composable<T>, expression: Context.(U) -> T): (U) -> T = { u ->
    listOf(
        *composers,
        ArgsExpressionComposer(expression, u)
    )
        .reduceRight { rootComposer, acc -> rootComposer.compose(acc) }
        .invoke()
}

// ______________________________________________________
//
// @ Misc just a hobby ：）
operator fun <T> Composable<T>.times(composable: Composable<T>) = listOf(this, composable)

operator fun <T> Collection<Composable<T>>.plus(expression: Context.() -> T): T = kompose(*this.toTypedArray(), expression = expression)
