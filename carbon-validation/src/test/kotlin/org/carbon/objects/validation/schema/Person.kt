package org.carbon.objects.validation.schema

import org.carbon.objects.validation.Definition
import org.carbon.objects.validation.Validated
import org.carbon.objects.validation.invalidate
import org.carbon.objects.validation.matcher.Email
import org.carbon.objects.validation.matcher.IncludeShape.AnyOfChar
import org.carbon.objects.validation.matcher.Reg
import org.carbon.objects.validation.matcher.WithIn
import org.carbon.objects.validation.matcher.and
import org.carbon.objects.validation.matcher.be
import org.carbon.objects.validation.matcher.eq
import org.carbon.objects.validation.matcher.include
import org.carbon.objects.validation.matcher.max
import org.carbon.objects.validation.matcher.mayBe
import org.carbon.objects.validation.matcher.min
import org.carbon.objects.validation.matcher.or
import org.carbon.objects.validation.matcher.withIn

data class Person(
    val name: String,
    val password: String,
    val passwordConfirm: String,
    val onetimePw: Int?,
    val emails: List<String>,
    val age: Int,
    val phoneNumber: String?,
    val resume: Resume,
    val desiredCondition: DesiredCondition
) : Validated<Person> by PersonSchema

object PersonSchema : Validated<Person> {
    override val def: Definition<Person> = { person ->
        person.name should { and(it min 5, it max 10) } otherwise "name".invalidate()
        person.password should {
            and(
                or(
                    it include AnyOfChar("~!@#$%^&*()"),
                    it include AnyOfChar("1234567890")
                ),
                it min 8,
                it eq person.passwordConfirm
            )
        } otherwise "password".invalidate()

        person.onetimePw.should { it mayBe WithIn(0..9999) } otherwise "onetimePw".invalidate()

        person.emails shouldEach { it be Email } otherwise "emails".invalidate()

        person.age should { it withIn 0..150 } otherwise "age".invalidate()

        person.phoneNumber.should { it mayBe Reg("(?:\\d{3}-){2}\\d{4}") } otherwise "phoneNumber".invalidate()

        person.resume.shouldValidated() otherwise "resume".invalidate()

        person.desiredCondition.shouldValidated() otherwise "desiredCondition".invalidate()
    }
}
