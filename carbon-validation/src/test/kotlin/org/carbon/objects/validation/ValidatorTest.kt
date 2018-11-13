package org.carbon.objects.validation

import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.collections.shouldContainAll
import io.kotlintest.matchers.types.shouldBeTypeOf
import io.kotlintest.should
import io.kotlintest.shouldBe
import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.evaluation.rejection.CompositeRejection
import org.carbon.objects.validation.evaluation.rejection.UnitRejection
import org.carbon.objects.validation.evaluation.source.BasicCode
import org.carbon.objects.validation.evaluation.source.CompositionCode
import org.carbon.objects.validation.evaluation.source.IncludeCode
import org.carbon.objects.validation.evaluation.source.LengthCode
import org.carbon.objects.validation.evaluation.source.NumberCode
import org.carbon.objects.validation.evaluation.source.StringCode
import org.carbon.objects.validation.input.ClassRoomInput
import org.carbon.objects.validation.input.FruitInput
import org.carbon.objects.validation.input.Input
import org.carbon.objects.validation.input.PersonInput
import org.carbon.objects.validation.template.Expected
import org.carbon.objects.validation.template.ParameterTemplate
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

/**
 * @author Soda 2018/10/08.
 */
class ValidatorTest {
    class StatementBaseExpected : Expected<StatementBaseExpected>() {
        override val self: StatementBaseExpected
            get() = this

        fun toBeObservance(): StatementBaseExpected = withAssertion { res ->
            res.shouldBeTypeOf<Evaluation.Accepted>()
        }

        fun toBeViolation(): StatementBaseExpected = withAssertion { res ->
            res should { (it is Evaluation.Rejected).shouldBeTrue() }
        }

        fun hasNameViolation(): StatementBaseExpected = withAssertion { res ->
            val rejection = (res as Evaluation.Rejected)["name"]
            checkNotNull(rejection) { "key name should be exist" }
            rejection.source.code.canonicalName shouldBe LengthCode.Max.canonicalName
            rejection.source.params[0] shouldBe 10
        }

        fun hasEqualViolation(param1: String, param2: String): StatementBaseExpected = withAssertion { res ->
            val rejection = (res as Evaluation.Rejected)["password"]
            checkNotNull(rejection) { "key password should be exist" }
            rejection.source.code.canonicalName shouldBe BasicCode.Equal.canonicalName
            rejection.source.params[0] shouldBe param1
            rejection.source.params[1] shouldBe param2
        }

        fun hasEmailViolation(at: Int): StatementBaseExpected = withAssertion { res ->
            val rejection = (res as Evaluation.Rejected)["emails[$at]"]
            checkNotNull(rejection) { "key emails[$at] should be exist" }
            rejection.source.code.canonicalName shouldBe StringCode.Email.canonicalName
            rejection.source.params should { it.isEmpty().shouldBeTrue() }
        }

        fun hasPasswordTooShortViolation(): StatementBaseExpected = withAssertion { res ->
            val rejection = (res as Evaluation.Rejected)["password"]
            checkNotNull(rejection) { "key password should be exist" }
            rejection.source.code.canonicalName shouldBe LengthCode.Min.canonicalName
            rejection.source.params[0] shouldBe 8
        }

        fun hasPasswordRequirementsViolation(): StatementBaseExpected = withAssertion { res ->
            val rejection = (res as Evaluation.Rejected)["password"]
            checkNotNull(rejection) { "key password should be exist" }

            val andNode = rejection.source.params[0]
            val orNodes = (andNode as CompositeRejection<*>).source.params

            val orNode1 = orNodes[0]
            orNode1 should { (it is UnitRejection<*>).shouldBeTrue() }
            val orNodeSource1 = (orNode1 as UnitRejection<*>).source
            orNodeSource1.code.canonicalName shouldBe IncludeCode.Any.canonicalName
            orNodeSource1.params.all().shouldContainAll("~!@#$%^&*()".toCharArray().map(Char::toString))

            val orNode2 = orNodes[1]
            orNode2 should { (it is UnitRejection<*>).shouldBeTrue() }
            val orNodeSource2 = (orNode2 as UnitRejection<*>).source
            orNodeSource2.code.canonicalName shouldBe IncludeCode.Any.canonicalName
            orNodeSource2.params.all().shouldContainAll("1234567890".toCharArray().map(Char::toString))
        }

        fun hasAgeNoWayViolation(): StatementBaseExpected = withAssertion { res ->
            val rejection = (res as Evaluation.Rejected)["age"]
            checkNotNull(rejection) { "key age should be exist" }
            rejection.source.code.canonicalName shouldBe LengthCode.Range.canonicalName
            rejection.source.params[0] shouldBe 0
            rejection.source.params[1] shouldBe 150
        }


        fun hasIllegalURLViolation(): StatementBaseExpected = withAssertion { res ->
            val rejection = (res as Evaluation.Rejected)["resume.portfolioUrl"]
            checkNotNull(rejection) { "key resume.portfolioUrl should be exist" }
            rejection.source.code.canonicalName shouldBe StringCode.URL.canonicalName
        }

        fun hasIllegalIncomeViolation(): StatementBaseExpected = withAssertion { res ->
            val rejection = (res as Evaluation.Rejected)["resume.income"]
            checkNotNull(rejection) { "resume.income should be exist" }
            rejection.key.qualifiedName shouldBe "resume.income"
            rejection.source.code.canonicalName shouldBe NumberCode.Natural.canonicalName
        }

        fun hasNotMentionedCertification(): StatementBaseExpected = withAssertion { res ->
            val rejection = (res as Evaluation.Rejected)["resume.text"]
            checkNotNull(rejection) { "resume.text should be exist" }
            rejection.source.code.canonicalName shouldBe IncludeCode.All.canonicalName
        }

        fun hasIllegalPhoneNumber(): StatementBaseExpected = withAssertion { res ->
            val rejection = (res as Evaluation.Rejected)["phoneNumber"]
            checkNotNull(rejection) { "phoneNumber should be exist" }
            rejection.source.code.canonicalName shouldBe StringCode.Regex.canonicalName
        }

        fun hasCheerUpPersonToDemandMonthlyIncomeViolation(): StatementBaseExpected = withAssertion { res ->
            val rejection = (res as Evaluation.Rejected)["desiredCondition.monthlyIncome"]
            checkNotNull(rejection) { "desiredCondition.monthlyIncome should be exist" }
            rejection.source.code.canonicalName shouldBe LengthCode.Min.canonicalName
        }

        fun hasOnePasswordRequirementsViolation(): StatementBaseExpected = withAssertion { res ->
            val rejection = (res as Evaluation.Rejected)["onetimePw"]
            checkNotNull(rejection) { "onetimePw should be exist" }
            rejection.source.code.canonicalName shouldBe LengthCode.Range.canonicalName
            rejection.source.params[0] shouldBe 0
            rejection.source.params[1] shouldBe 9999
        }

        fun hasMergedBananaViolation(): StatementBaseExpected = withAssertion { res ->
            val rejection = (res as Evaluation.Rejected)["name"]
            checkNotNull(rejection) { "name should be exist" }
            rejection.source.code.canonicalName shouldBe CompositionCode.And.canonicalName
            rejection.source.params.size shouldBe 2
            rejection.source.params[0].shouldBeTypeOf<UnitRejection<Any>>()
            rejection.source.params[1].shouldBeTypeOf<UnitRejection<Any>>()
        }
    }

    companion object : ParameterTemplate() {
        @Suppress("unused")
        @JvmStatic
        override fun data() = listOf(
                case("[observance] no violation")(
                        PersonInput(),
                        StatementBaseExpected().toBeObservance()),
                case("[violation] name too long")(
                        PersonInput().name("too long name..."),
                        StatementBaseExpected().toBeViolation().hasNameViolation()),
                case("[violation] correlation violation")(
                        PersonInput().password("password1").password2("hogehoge"),
                        StatementBaseExpected().toBeViolation().hasEqualViolation(param1 = "password1", param2 = "hogehoge")),
                case("[violation] with suitable index")(
                        PersonInput().emails("email@valid.com", "email@..invalid"),
                        StatementBaseExpected().toBeViolation().hasEmailViolation(at = 1)),
                case("[violation] password too short violation")(
                        PersonInput().password("short$").password2("short$"),
                        StatementBaseExpected().toBeViolation().hasPasswordTooShortViolation()),
                case("[violation] password doesn't meet requirements")(
                        PersonInput().password("hogehoge"),
                        StatementBaseExpected().toBeViolation().hasPasswordRequirementsViolation()),
                case("[violation] onetime password doesn't meet requirements")(
                        PersonInput().onetimePw(12345),
                        StatementBaseExpected().toBeViolation().hasOnePasswordRequirementsViolation()),
                case("[violation] person is phoenix")(
                        PersonInput().age(1234),
                        StatementBaseExpected().toBeViolation().hasAgeNoWayViolation()),
                case("[violation] person wanna be jobless")(
                        PersonInput().desiredMonthlyIncome(0),
                        StatementBaseExpected().toBeViolation().hasCheerUpPersonToDemandMonthlyIncomeViolation()
                ),
                case("[violation] phone number is illegal")(
                        PersonInput().phoneNumber("123----0"),
                        StatementBaseExpected().toBeViolation().hasIllegalPhoneNumber()),
                case("[violation] portfolio is invalid url")(
                        PersonInput().portfolioUrl("invalid:$/url.com"),
                        StatementBaseExpected().toBeViolation().hasIllegalURLViolation()),
                case("[violation] income is negative")(
                        PersonInput().income(-1000),
                        StatementBaseExpected().toBeViolation().hasIllegalIncomeViolation()),
                case("[violation] certification is not mentioned at resume")(
                        PersonInput()
                                .certifications(listOf(
                                        "Oracle Certified Java Programmer",
                                        "AWS Certified Solutions Architect"))
                                .resumeText("I have over 1 year of experience developing Kotlin and Swift"),
                        StatementBaseExpected().toBeViolation().hasNotMentionedCertification()),
                case("[violation] apple should be apple")(
                        FruitInput().apple(),
                        StatementBaseExpected().toBeObservance()
                ),
                case("[violation] banana must not be apple with merge test")(
                        FruitInput().fakeBanana(),
                        StatementBaseExpected().toBeViolation()
                                .hasMergedBananaViolation()
                ),
                case("[observance] nested validation success")(
                        ClassRoomInput()
                                .person { }
                                .person { }
                                .person { },
                        StatementBaseExpected().toBeObservance()),
                case("[violation] nested data violation")(
                        ClassRoomInput()
                                .person {
                                    name("too long name...")
                                    certifications(listOf("NONE"))
                                }
                                .person {
                                    password("password1")
                                    password2("hogehoge")
                                }
                                .person {
                                    emails("email@valid.com", "email@..invalid")
                                },
                        StatementBaseExpected().toBeViolation())
        )
    }

    @ParameterizedTest(name = "Validate Test[{index}] {0}")
    @MethodSource("data")
    fun validate(describe: String, input: Input, expected: StatementBaseExpected) {
        expected.assert(describe, input::tryValidate)
    }
}
