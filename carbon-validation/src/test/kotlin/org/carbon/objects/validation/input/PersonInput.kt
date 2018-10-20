package org.carbon.objects.validation.input

import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.schema.Person
import org.carbon.objects.validation.validate

class PersonInput : Input {
    private var _name: String = "shota oda"
    private var _password: String = "password1"
    private var _password2: String = "password1"
    private var _emails: List<String> = listOf("shota@cbn.org")
    private var _age: Int = 20
    private var _portfolioUrl = "https://github.com/ShotaOd"
    private var _income = 90000

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

    override fun value(): Person = Person(_name, _password, _password2, _emails, _age, _portfolioUrl, _income)

    override fun tryValidate(): Evaluation = value().validate()
}