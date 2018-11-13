package org.carbon.objects.validation.input

import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.schema.ClassRoom
import org.carbon.objects.validation.schema.Person
import org.carbon.objects.validation.validate

class ClassRoomInput : Input {
    private val _people: MutableList<Person> = mutableListOf()
    fun person(personBuilder: PersonInput.() -> Unit): ClassRoomInput {
        val input = PersonInput()
        personBuilder(input)
        _people.add(input.value())
        return this
    }

    override fun value(): ClassRoom = ClassRoom(_people)

    override fun tryValidate(): Evaluation = value().validate()
}
