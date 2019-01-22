package org.carbon.composer

import io.kotlintest.shouldBe
import org.carbon.composer.KomposerKtTest.ForHereOrToGo.ForHere
import org.carbon.composer.KomposerKtTest.ForHereOrToGo.ToGo
import org.carbon.composer.KomposerKtTest.Pos.Bot
import org.carbon.composer.KomposerKtTest.Pos.Top
import org.junit.jupiter.api.Test

@Suppress("MemberVisibilityCanBePrivate", "PropertyName")
class KomposerKtTest {

    // ______________________________________________________
    //
    // @ Hamburger Shop Ingredients
    object Buns : Composable<String>() {
        const val crown = "🍔🍔🍔🍔"
        const val heel = "🍞🍞🍞🍞"
        override fun invoke(): String = listOf(crown, super.callChild(), heel)
            .joinToString("\n", prefix = "\n", postfix = "\n")
    }

    enum class Pos {
        Top, Bot,
    }

    abstract class Topping(val value: String, val pos: Pos) : Composable<String>() {
        override fun invoke(): String =
            when (pos) {
                Top -> listOf(value, super.callChild())
                Bot -> listOf(super.callChild(), value)
            }.joinToString("\n")
    }

    class Cheese(pos: Pos) : Topping("🧀🧀🧀🧀", pos)
    class Tomato(pos: Pos) : Topping("🍅🍅🍅🍅", pos)

    val Bacon = "🥓🥓🥓🥓"

    @Test
    fun i_order_hamburger() {
        // when
        fun order(): String = kompose(Buns, Tomato(Top), Cheese(Bot)) { Bacon }

        // then
        order() shouldBe """

            🍔🍔🍔🍔
            🍅🍅🍅🍅
            🥓🥓🥓🥓
            🧀🧀🧀🧀
            🍞🍞🍞🍞

        """.trimIndent()
    }

    enum class ForHereOrToGo {
        ForHere, ToGo
    }

    @Test
    fun i_order_hamburger_if_asked() {
        // given
        fun atRegister(ask: ForHereOrToGo): ((String) -> String) -> String = { order ->
            val service = when (ask) {
                ForHere -> "🍽"
                ToGo -> "🥡"
            }
            order(service)
        }

        val order: (String) -> String = kompose1(Buns, Tomato(Top), Cheese(Bot)) { service ->
            "$Bacon 👐 $service"
        }

        // when
        atRegister(ForHere)(order) shouldBe """

            🍔🍔🍔🍔
            🍅🍅🍅🍅
            🥓🥓🥓🥓 👐 🍽
            🧀🧀🧀🧀
            🍞🍞🍞🍞

        """.trimIndent()

        // when
        atRegister(ToGo)(order) shouldBe """

            🍔🍔🍔🍔
            🍅🍅🍅🍅
            🥓🥓🥓🥓 👐 🥡
            🧀🧀🧀🧀
            🍞🍞🍞🍞

        """.trimIndent()
    }

    data class PlayGameEmotion(val describe: String)
    class PlayGame : Composable<String>() {
        override fun invoke(): String {
            // play the game ...
            // and then ... win!
            context.set(PlayGameEmotion("😎"))
            return super.callChild()
        }
    }

    data class DoHomeworkEmotion(val describe: String)
    class DoHomework : Composable<String>() {
        override fun invoke(): String {
            // do homework ...
            // and then ... it's too difficult to do
            context.set(DoHomeworkEmotion("🤔"))
            return super.callChild()
        }
    }

    data class WatchMovieEmotion(val describe: String)
    class WatchMovie : Composable<String>() {
        override fun invoke(): String {
            // watch a heartwarming movie
            // and then ... be deeply moved
            context.set(WatchMovieEmotion("😊"))
            return super.callChild()
        }
    }

    @Test
    internal fun i_write_a_diary_of_today() {
        // when
        val impressionsOfToday = kompose(PlayGame(), DoHomework(), WatchMovie()) {
            """
                played the game in the morning. ${context[PlayGameEmotion::class].describe}
                In the afternoon I finished my homework, ${context[DoHomeworkEmotion::class].describe}
                Then I went to the cinema ${context[WatchMovieEmotion::class].describe}
            """.trimIndent()
        }

        // then
        impressionsOfToday shouldBe
            """
                played the game in the morning. 😎
                In the afternoon I finished my homework, 🤔
                Then I went to the cinema 😊
            """.trimIndent()
    }
}