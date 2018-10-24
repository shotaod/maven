package org.carbon.objects.validation

import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.collections.shouldContainAll
import io.kotlintest.matchers.types.shouldBeTypeOf
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.evaluation.rejection.CompositeRejection
import org.carbon.objects.validation.evaluation.rejection.RootRejection
import org.carbon.objects.validation.evaluation.rejection.UnitRejection
import org.carbon.objects.validation.evaluation.source.BasicCode
import org.carbon.objects.validation.evaluation.source.IncludeCode
import org.carbon.objects.validation.evaluation.source.LengthCode
import org.carbon.objects.validation.evaluation.source.NumberCode
import org.carbon.objects.validation.evaluation.source.StringCode
import org.carbon.objects.validation.input.ClassRoomInput
import org.carbon.objects.validation.input.FruitInput
import org.carbon.objects.validation.input.IllegalMaxInput
import org.carbon.objects.validation.input.IllegalMinInput
import org.carbon.objects.validation.input.IllegalWithInInput
import org.carbon.objects.validation.input.Input
import org.carbon.objects.validation.input.PersonInput
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.reflect.KClass

/**
 * @author Soda 2018/10/08.
 */
typealias Assertion = (Evaluation) -> Unit

class ValidatorTest {
    open class Expected {
        private var _assertions: List<Assertion> = emptyList()

        open fun assert(describe: String, evalExp: () -> Evaluation) {
            val evaluation = evalExp()
            println("""
                --------------------------------------------------
                $describe
                --------------------------------------------------

                """.trimIndent() + evaluation.describe())
            _assertions.forEach { it(evaluation) }
        }

        private fun withAssertion(assertion: Assertion): Expected {
            _assertions += assertion
            return this
        }

        fun toBeObservance(): Expected = withAssertion { res ->
            res.shouldBeTypeOf<Evaluation.Acceptance>()
        }

        fun toBeViolation(): Expected = withAssertion { res ->
            res should { (it is Evaluation.Rejection<*>).shouldBeTrue() }
        }

        fun hasNameViolation(): Expected = withAssertion { res ->
            val rejection = (res as RootRejection)["name"]
            checkNotNull(rejection) { " key name should be exist" }
            rejection!!.source.code shouldBe LengthCode.Max
            rejection.source.params[0] shouldBe 10
        }

        fun hasEqualViolation(param1: String, param2: String): Expected = withAssertion { res ->
            val rejection = (res as RootRejection)["password"]
            checkNotNull(rejection) { "key password should be exist" }
            rejection!!.source.code shouldBe BasicCode.Equal
            rejection.source.params[0] shouldBe param1
            rejection.source.params[1] shouldBe param2
        }


        fun hasEmailViolation(at: Int): Expected = withAssertion { res ->
            val rejection = (res as RootRejection)["emails[$at]"]
            checkNotNull(rejection) { "key emails[$at] should be exist" }
            rejection!!.source.code shouldBe StringCode.Email
            rejection.source.params should { it.isEmpty().shouldBeTrue() }
        }


        fun hasPasswordTooShortViolation(): Expected = withAssertion { res ->
            val rejection = (res as RootRejection)["password"]
            checkNotNull(rejection) { "key password should be exist" }
            rejection!!.source.code shouldBe LengthCode.Min
            rejection.source.params[0] shouldBe 8
        }


        fun hasPasswordRequirementsViolation(): Expected = withAssertion { res ->
            val rejection = (res as RootRejection)["password"]
            checkNotNull(rejection) { "key password should be exist" }

            val andNode = rejection!!.source.params[0]
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


        fun hasAgeNoWayViolation(): Expected = withAssertion { res ->
            val rejection = (res as RootRejection)["age"]
            checkNotNull(rejection) { "key age should be exist" }
            rejection!!.source.code shouldBe LengthCode.Max
            rejection.source.params[0] shouldBe 150
        }


        fun hasIllegalURLViolation(): Expected = withAssertion { res ->
            val rejection = (res as RootRejection)["resume.portfolioUrl"]
            checkNotNull(rejection) { "key resume.portfolioUrl should be exist" }
            rejection!!.source.code shouldBe StringCode.URL
        }


        fun hasIllegalIncomeViolation(): Expected = withAssertion { res ->
            val rejection = (res as RootRejection)["resume.income"]
            checkNotNull(rejection) { "resume.income should be exist" }
            rejection!!.key.qualifiedName shouldBe "resume.income"
            rejection.source.code shouldBe NumberCode.Natural
        }


        fun hasNotMentionedCertification(): Expected = withAssertion { res ->
            val rejection = (res as RootRejection)["resume.text"]
            checkNotNull(rejection) { "resume.text should be exist" }
            rejection!!.source.code shouldBe IncludeCode.All
        }


        fun hasIllegalPhoneNumber(): Expected = withAssertion { res ->
            val rejection = (res as RootRejection)["phoneNumber"]
            checkNotNull(rejection) { "phoneNumber should be exist" }
            rejection!!.source.code shouldBe StringCode.Regex
        }


        fun hasCheerUpPersonToDemandMonthlyIncomeViolation(): Expected = withAssertion { res ->
            val rejection = (res as RootRejection)["desiredCondition.monthlyIncome"]
            checkNotNull(rejection) { "desiredCondition.monthlyIncome should be exist" }
            rejection!!.source.code shouldBe LengthCode.Min
        }


        fun hasOnePasswordRequirementsViolation(): Expected = withAssertion { res ->
            val rejection = (res as RootRejection)["onetimePw"]
            checkNotNull(rejection) { "onetimePw should be exist" }
            rejection!!.source.code shouldBe LengthCode.Max
            rejection.source.params[0] shouldBe 9999
        }
    }

    class ThrowExpected : Expected() {
        override fun assert(describe: String, evalExp: () -> Evaluation) {
            println("""
                --------------------------------------------------
                $describe
                --------------------------------------------------
                throw -> ${type.simpleName}
            """.trimIndent())
            assertThrow(evalExp)
        }

        var assertThrow: (eval: () -> Evaluation) -> Unit = {}
        var type: KClass<*> = Exception::class

        inline fun <reified T : Throwable> throws(): Expected {
            type = T::class
            assertThrow = { it -> shouldThrow<T>(it) }
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
                case("[violation] onetime password doesn't meet requirements")(
                        PersonInput().onetimePw(12345),
                        Expected().toBeViolation().hasOnePasswordRequirementsViolation()),
                case("[violation] person is phoenix")(
                        PersonInput().age(1234),
                        Expected().toBeViolation().hasAgeNoWayViolation()),
                case("[violation] person wanna be jobless")(
                        PersonInput().desiredMonthlyIncome(0),
                        Expected().toBeViolation().hasCheerUpPersonToDemandMonthlyIncomeViolation()
                ),
                case("[violation] phone number is illegal")(
                        PersonInput().phoneNumber("123----0"),
                        Expected().toBeViolation().hasIllegalPhoneNumber()),
                case("[violation] portfolio is invalid url")(
                        PersonInput().portfolioUrl("invalid:$/url.com"),
                        Expected().toBeViolation().hasIllegalURLViolation()),
                case("[violation] income is negative")(
                        PersonInput().income(-1000),
                        Expected().toBeViolation().hasIllegalIncomeViolation()),
                case("[violation] certification is not mentioned at resume")(
                        PersonInput()
                                .certifications(listOf(
                                        "Oracle Certified Java Programmer",
                                        "AWS Certified Solutions Architect"))
                                .resumeText("I have over 1 year of experience developing Kotlin and Swift"),
                        Expected().toBeViolation().hasNotMentionedCertification()),
                case("[violation] apple should be apple")(
                        FruitInput().apple(),
                        Expected().toBeObservance()
                ),
                case("[violation] banana must not be apple with merge test")(
                        FruitInput().fakeBanana(),
                        Expected().toBeViolation()
                ),
                case("[observance] nested validation success")(
                        ClassRoomInput()
                                .person(PersonInput())
                                .person(PersonInput())
                                .person(PersonInput()),
                        Expected().toBeObservance()),
                case("[violation] nested data violation")(
                        ClassRoomInput()
                                .person(PersonInput().name("too long name...").certifications(listOf("NONE")))
                                .person(PersonInput().password("password1").password2("hogehoge"))
                                .person(PersonInput().emails("email@valid.com", "email@..invalid")),
                        Expected().toBeViolation()),
                case("[illegal] schema definition is illegal min")(
                        IllegalMinInput(),
                        ThrowExpected().throws<IllegalArgumentException>()),
                case("[illegal] schema definition is illegal max")(
                        IllegalMaxInput(),
                        ThrowExpected().throws<IllegalArgumentException>()),
                case("[illegal] schema definition is illegal within")(
                        IllegalWithInInput(),
                        ThrowExpected().throws<IllegalArgumentException>())
        )

        private val case: (describe: String) -> (input: Input, expected: Expected) -> Array<Any> = { describe ->
            { input, expected ->
                arrayOf(describe, input, expected)
            }
        }
    }

    @ParameterizedTest(name = "Validate Test[{index}] {0}")
    @MethodSource("data")
    fun validate(describe: String, input: Input, expected: Expected) {
        expected.assert(describe, input::tryValidate)
    }
}