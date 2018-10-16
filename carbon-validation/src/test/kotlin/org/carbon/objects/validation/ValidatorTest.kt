package org.carbon.objects.validation

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

/**
 * @author Soda 2018/10/08.
 */
class ValidatorTest {
    data class Person(
            val name: String,
            val password: String,
            val password2: String,
            val emails: List<String>
    ) : Validated<Person> by PersonSchema

    object PersonSchema : Validated<Person> {
        override val def: Definition<Person> = {
            it.name should { and(it min 5, it max 10) } otherwise Specify("name")
            //it.password should { it minEq 8 } otherwise Specify("password")
            //it.password2 should { it minEq 8 } otherwise Specify("password2")
            val p2 = it.password2
            it.password should { it eq p2 } otherwise Specify("password")

            it.emails.forEachIndexed { i, email ->
                email should { it be Email } otherwise Specify("emails", at = i)
            }
        }
    }

    class Input {
        private var _name: String = "shota oda"
        private var _password: String = "password"
        private var _password2: String = "password"
        private var _emails: List<String> = listOf("shota@cbn.org")

        fun name(s: String): Input {
            _name = s
            return this
        }

        fun password(p: String): Input {
            _password = p
            return this
        }

        fun password2(p: String): Input {
            _password2 = p
            return this
        }

        fun emails(vararg s: String): Input {
            _emails = s.toList()
            return this
        }

        fun person() = Person(_name, _password, _password2, _emails)
    }

    class Expected {
        private lateinit var _assert: (vr: ValidationResult) -> Unit

        fun toBe(assert: (ValidationResult) -> Unit): Expected {
            _assert = assert
            return this
        }

        val assert get() = _assert

        fun toBeObservance() = toBe { res -> res is ObservanceResult<*> }

        fun toBeViolation(assertDetail: (vs: ViolationList) -> Unit = {}) = toBe { res ->
            assertTrue { res is ViolationResult }
            val vr = res as ViolationResult
            vr.violations.forEach(::println)
            assertDetail(vr.violations)
        }
    }

    companion object {
        @JvmStatic
        fun data() = listOf(
                case("noc violation")(Input(), Expected().toBeObservance()),
                case("single violation")(Input().name("too long name..."), Expected().toBeViolation()),
                case("composite violation")(Input().password("password").password2("hogehoge"), Expected().toBeViolation()),
                case("list violation")(Input().emails("email@valid.com", "email@..invalid"), Expected().toBeViolation())
        )

        private val case: (describe: String) -> (input: Input, expected: Expected) -> Array<*> = { describe ->
            { input, expected ->
                arrayOf(describe, input, expected)
            }
        }
    }

    @ParameterizedTest(name = "Validate Test[{index}] {0}")
    @MethodSource("data")
    @Suppress("UNUSED_PARAMETER")
    fun validate(describe: String, input: Input, expected: Expected) {
        val person = input.person()
        val result = Validator.validate(person)
        expected.assert(result)
    }
}