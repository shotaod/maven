package org.carbon.objects.validation.schema

import org.carbon.objects.validation.Definition
import org.carbon.objects.validation.Validated
import org.carbon.objects.validation.invalidate
import org.carbon.objects.validation.matcher.Email
import org.carbon.objects.validation.matcher.IncludeShape.AnyOfChar
import org.carbon.objects.validation.matcher.Natural
import org.carbon.objects.validation.matcher.URL
import org.carbon.objects.validation.matcher.and
import org.carbon.objects.validation.matcher.be
import org.carbon.objects.validation.matcher.eq
import org.carbon.objects.validation.matcher.include
import org.carbon.objects.validation.matcher.max
import org.carbon.objects.validation.matcher.min
import org.carbon.objects.validation.matcher.or
import org.carbon.objects.validation.matcher.withIn

data class Person(
        val name: String,
        val password: String,
        val password2: String,
        val emails: List<String>,
        val age: Int,
        val portfolioUrl: String,
        val income: Int
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
                    it eq person.password2
            )
        } otherwise "password".invalidate()

        person.emails shouldEach { it be Email } otherwise "emails".invalidate()

        person.age should { it withIn 0..150 } otherwise "age".invalidate()

        person.portfolioUrl should { it be URL } otherwise "portfolioUrl".invalidate()

        person.income should { it be Natural } otherwise "income".invalidate()
    }
}
