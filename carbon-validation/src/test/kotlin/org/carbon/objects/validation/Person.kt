package org.carbon.objects.validation

import org.carbon.objects.validation.matcher.Email
import org.carbon.objects.validation.matcher.IncludeShape.Companion.OneOfChar
import org.carbon.objects.validation.matcher.and
import org.carbon.objects.validation.matcher.be
import org.carbon.objects.validation.matcher.eq
import org.carbon.objects.validation.matcher.include
import org.carbon.objects.validation.matcher.max
import org.carbon.objects.validation.matcher.min
import org.carbon.objects.validation.matcher.or

data class Person(
        val name: String,
        val password: String,
        val password2: String,
        val emails: List<String>,
        val address: List<String>
) : Validated<Person> by PersonSchema

object PersonSchema : Validated<Person> {
    override val def: Definition<Person> = { person ->
        person.name should { and(it min 5, it max 10) } otherwise "name".invalidate()
        person.password should {
            or(
                    it include OneOfChar("~!@#$%^&*()"),
                    it include OneOfChar("1234567890")
            )
        } otherwise "password".invalidate()

        person.password should { it eq person.password2 } otherwise "password".invalidate()

        person.emails shouldEach { it be Email } otherwise "emails".invalidate()
    }
}
