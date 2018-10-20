package org.carbon.objects.validation

import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.collections.shouldContainAll
import io.kotlintest.matchers.types.shouldBeTypeOf
import io.kotlintest.should
import io.kotlintest.shouldBe
import org.carbon.objects.validation.evaluation.CompositeRejection
import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.evaluation.UnitRejection
import org.carbon.objects.validation.evaluation.source.BasicCode
import org.carbon.objects.validation.evaluation.source.CompositionCode
import org.carbon.objects.validation.evaluation.source.IncludeCode
import org.carbon.objects.validation.evaluation.source.LengthCode
import org.carbon.objects.validation.evaluation.source.NumberCode
import org.carbon.objects.validation.evaluation.source.StringCode
import org.carbon.objects.validation.input.ClassRoomInput
import org.carbon.objects.validation.input.Input
import org.carbon.objects.validation.input.PersonInput
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

/**
 * @author Soda 2018/10/08.
 */
class ValidatorTest {
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
                rejection.source.code shouldBe CompositionCode.And
                val andNode = rejection.source.params[0]
                andNode should { (it is UnitRejection<*>).shouldBeTrue() }
                val andNodeSource = (andNode as UnitRejection<*>).source
                andNodeSource.code shouldBe BasicCode.Equal
                andNodeSource.params[0] shouldBe param1
                andNodeSource.params[1] shouldBe param2
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


        fun hasPasswordTooShortViolation(): Expected {
            _assertions += { res ->
                val rejection = (res as Evaluation.RootRejection).rejections[0]
                rejection.key.qualifiedName shouldBe "password"
                rejection.source.code shouldBe CompositionCode.And
                val andNode = rejection.source.params[0]
                andNode should { (it is UnitRejection<*>).shouldBeTrue() }
                val andNodeSource = (andNode as UnitRejection<*>).source
                andNodeSource.code shouldBe LengthCode.Min
                andNodeSource.params[0] shouldBe 8
            }
            return this
        }

        fun hasPasswordRequirementsViolation(): Expected {
            _assertions += { res ->
                val rejection = (res as Evaluation.RootRejection).rejections[0]
                rejection.key.qualifiedName shouldBe "password"
                rejection.source.code shouldBe CompositionCode.And

                val andNode = rejection.source.params[0]
                val orNodes = (andNode as CompositeRejection<*>).source.params

                val orNode1 = orNodes[0]
                orNode1 should { (it is UnitRejection<*>).shouldBeTrue() }
                val orNodeSource1 = (orNode1 as UnitRejection<*>).source
                orNodeSource1.code shouldBe IncludeCode.Any
                orNodeSource1.params.all().shouldContainAll("~!@#$%^&*()".toCharArray().map(Char::toString))

                val orNode2 = orNodes[1]
                orNode2 should { (it is UnitRejection<*>).shouldBeTrue() }
                val orNodeSource2 = (orNode2 as UnitRejection<*>).source
                orNodeSource2.code shouldBe IncludeCode.Any
                orNodeSource2.params.all().shouldContainAll("1234567890".toCharArray().map(Char::toString))
            }
            return this
        }

        fun hasAgeNoWayViolation(): Expected {
            _assertions += { res ->
                val rejection = (res as Evaluation.RootRejection).rejections[0]
                rejection.key.qualifiedName shouldBe "age"
                rejection.source.code shouldBe LengthCode.Range
            }
            return this
        }

        fun hasIllegalURLViolation(): Expected {
            _assertions += { res ->
                val rejection = (res as Evaluation.RootRejection).rejections[0]
                rejection.key.qualifiedName shouldBe "portfolioUrl"
                rejection.source.code shouldBe StringCode.URL
            }
            return this
        }

        fun hasIllegalIncomeViolation(): Expected {
            _assertions += { res ->
                val rejection = (res as Evaluation.RootRejection).rejections[0]
                rejection.key.qualifiedName shouldBe "income"
                rejection.source.code shouldBe NumberCode.Natural
            }
            return this
        }
    }

    companion object {
        @Suppress("unused")
        @JvmStatic
        fun data() = listOf(
                case("[observance] no violation")(
                        PersonInput(),
                        Expected().toBeObservance()),
                case("[violation] name too long")(
                        PersonInput().name("too long name..."),
                        Expected().toBeViolation().hasNameViolation()),
                case("[violation] correlation violation")(
                        PersonInput().password("password1").password2("hogehoge"),
                        Expected().toBeViolation().hasEqualViolation(param1 = "password1", param2 = "hogehoge")),
                case("[violation] with suitable index")(
                        PersonInput().emails("email@valid.com", "email@..invalid"),
                        Expected().toBeViolation().hasEmailViolation(at = 1)),
                case("[violation] password too short violation")(
                        PersonInput().password("short$").password2("short$"),
                        Expected().toBeViolation().hasPasswordTooShortViolation()),
                case("[violation] password doesn't meet requirements")(
                        PersonInput().password("hogehoge"),
                        Expected().toBeViolation().hasPasswordRequirementsViolation()),
                case("[violation] person is phoenix")(
                        PersonInput().age(1234),
                        Expected().toBeViolation().hasAgeNoWayViolation()),
                case("[violation] portfolio is invalid url")(
                        PersonInput().portfolioUrl("invalid:$/url.com"),
                        Expected().toBeViolation().hasIllegalURLViolation()),
                case("[violation] income is negative")(
                        PersonInput().income(-1000),
                        Expected().toBeViolation().hasIllegalIncomeViolation()),
                case("[observance] nested validation success")(
                        ClassRoomInput()
                                .person(PersonInput())
                                .person(PersonInput())
                                .person(PersonInput()),
                        Expected().toBeObservance()),
                case("[violation] nested data violation")(
                        ClassRoomInput()
                                .person(PersonInput().name("too long name..."))
                                .person(PersonInput().password("password1").password2("hogehoge"))
                                .person(PersonInput().emails("email@valid.com", "email@..invalid")),
                        Expected().toBeViolation())
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