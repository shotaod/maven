package org.carbon.objects.validation

import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.types.shouldBeTypeOf
import io.kotlintest.should
import io.kotlintest.shouldBe
import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.evaluation.UnitRejection
import org.carbon.objects.validation.evaluation.source.BasicCode
import org.carbon.objects.validation.evaluation.source.CompositionCode
import org.carbon.objects.validation.evaluation.source.LengthCode
import org.carbon.objects.validation.evaluation.source.StringCode
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

/**
 * @author Soda 2018/10/08.
 */
class ValidatorTest {
    interface Input {
        fun value(): Any
        fun tryValidate(): Evaluation
    }

    class PersonInput : Input {
        private var _name: String = "shota oda"
        private var _password: String = "password1"
        private var _password2: String = "password1"
        private var _emails: List<String> = listOf("shota@cbn.org")
        private var _address: List<String> = listOf()

        fun name(s: String): PersonInput {
            _name = s
            return this
        }

        fun password(p: String): PersonInput {
            _password = p
            return this
        }

        fun password2(p: String): PersonInput {
            _password2 = p
            return this
        }

        fun emails(vararg s: String): PersonInput {
            _emails = s.toList()
            return this
        }

        override fun value(): Person = Person(_name, _password, _password2, _emails, _address)

        override fun tryValidate(): Evaluation = value().validate()
    }

    class ClassRoomInput : Input {
        private val _people: MutableList<Person> = mutableListOf()
        fun person(person: PersonInput): ClassRoomInput {
            _people.add(person.value())
            return this
        }

        override fun value(): ClassRoom = ClassRoom(_people)

        override fun tryValidate(): Evaluation = value().validate()
    }

    class Expected {
        private var _assertions: List<(evaluation: Evaluation) -> Unit> = emptyList()

        fun assert(evaluation: Evaluation) = _assertions.forEach { it(evaluation) }

        fun toBeObservance(): Expected {
            _assertions += { it.shouldBeTypeOf<Evaluation.Acceptance>() }
            return this
        }

        fun toBeViolation(): Expected {
            _assertions += { res ->
                res should { (it is Evaluation.Rejection<*>).shouldBeTrue() }
                println((res as Evaluation.Rejection<*>))
            }
            return this
        }

        fun hasNameViolation(): Expected {
            _assertions += { res ->
                val rejection = (res as Evaluation.RootRejection).rejections[0]
                rejection.key.qualifiedName shouldBe "name"
                rejection.source.code shouldBe CompositionCode.And
                val andNode = rejection.source.params[0]
                andNode should { (it is UnitRejection<*>).shouldBeTrue() }
                val andNodeSource = (andNode as UnitRejection<*>).source
                andNodeSource.code shouldBe LengthCode.Max
                andNodeSource.params[0] shouldBe 10
            }
            return this
        }

        fun hasEqualViolation(param1: String, param2: String): Expected {
            _assertions += { res ->
                val rejection = (res as Evaluation.RootRejection).rejections[0]
                rejection.key.qualifiedName shouldBe "password"
                rejection.source.code shouldBe BasicCode.Equal
                rejection.source.params[0] shouldBe param1
                rejection.source.params[1] shouldBe param2
            }
            return this
        }

        fun hasEmailViolation(at: Int): Expected {
            _assertions += { res ->
                val rejection = (res as Evaluation.RootRejection).rejections[0]
                rejection.key.qualifiedName shouldBe "emails[$at]"
                rejection.source.code shouldBe StringCode.Email
                rejection.source.params should { it.isEmpty().shouldBeTrue() }
            }
            return this
        }
    }

    companion object {
        @Suppress("unused")
        @JvmStatic
        fun data() = listOf(
                case("no violation")(PersonInput(), Expected().toBeObservance()),
                case("single violation")(
                        PersonInput().name("too long name..."),
                        Expected().toBeViolation().hasNameViolation()),
                case("composite violation")(
                        PersonInput().password("password1").password2("hogehoge"),
                        Expected().toBeViolation().hasEqualViolation(param1 = "password1", param2 = "hogehoge")
                ),
                case("list violation")(
                        PersonInput().emails("email@valid.com", "email@..invalid"),
                        Expected().toBeViolation().hasEmailViolation(at = 1)),
                case("nested validation success")(
                        ClassRoomInput()
                                .person(PersonInput())
                                .person(PersonInput())
                                .person(PersonInput()),
                        Expected().toBeObservance()
                ),
                case("nested violation")(
                        ClassRoomInput()
                                .person(PersonInput().name("too long name..."))
                                .person(PersonInput().password("password1").password2("hogehoge"))
                                .person(PersonInput().emails("email@valid.com", "email@..invalid")),
                        Expected().toBeViolation()
                )
        )

        private val case: (describe: String) -> (input: Input, expected: Expected) -> Array<Any> = { describe ->
            { input, expected ->
                arrayOf(describe, input, expected)
            }
        }
    }

    @ParameterizedTest(name = "Validate Test[{index}] {0}")
    @MethodSource("data")
    @Suppress("UNUSED_PARAMETER")
    fun validate(describe: String, input: Input, expected: Expected) {
        val result = input.tryValidate()
        println(result.describe())
        expected.assert(result)
    }
}