package org.carbon.objects.validation.input

import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.schema.DesiredCondition
import org.carbon.objects.validation.schema.Person
import org.carbon.objects.validation.schema.Resume
import org.carbon.objects.validation.validate

class PersonInput : Input {
    override fun value(): Person = Person(
            _name,
            _password,
            _password2,
            _onetimePw,
            _emails,
            _age,
            _phoneNumber,
            Resume(
                    _portfolioUrl,
                    _income,
                    _certifications,
                    _resumeText
            ),
            DesiredCondition(
                    _desiredMonthlyIncome
            )
    )

    override fun tryValidate(): Evaluation = value().validate()

    private var _name: String = "shota oda"
    private var _password: String = "password1"
    private var _password2: String = "password1"
    private var _emails: List<String> = listOf("shota@cbn.org")
    private var _age: Int = 20
    private var _phoneNumber = "123-456-7890"
    // resume
    private var _portfolioUrl = "https://github.com/ShotaOd"
    private var _income = 90000
    private var _certifications: List<String> = listOf("Oracle Certified Java Programmer")
    private var _resumeText: String = """
        We are an industry leader in the design and implementation of application,
        and our clients include some of the largest manufacturers in North America.
        We are seeking a highly-qualified Engineer to take responsibility
        for the design and testing of solutions that meet our clients’ needs.
        In 2018, I got the qualification of Oracle Certified Java Programmer.
    """.trimIndent()
    // desired condition
    private var _desiredMonthlyIncome = 5000
    private var _onetimePw: Int = 1234

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

    fun age(age: Int): PersonInput {
        _age = age
        return this
    }

    fun portfolioUrl(url: String): PersonInput {
        _portfolioUrl = url
        return this
    }

    fun income(income: Int): PersonInput {
        _income = income
        return this
    }

    fun certifications(cers: List<String>): PersonInput {
        _certifications = cers
        return this
    }

    fun resumeText(resume: String): PersonInput {
        _resumeText = resume
        return this
    }

    fun phoneNumber(number: String): PersonInput {
        _phoneNumber = number
        return this
    }

    fun desiredMonthlyIncome(number: Int): PersonInput {
        _desiredMonthlyIncome = number
        return this
    }

    fun onetimePw(pw: Int): PersonInput {
        _onetimePw = pw
        return this
    }
}