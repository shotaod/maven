package org.carbon.objects.validation.input

import org.carbon.objects.validation.schema.ClassRoom
import org.carbon.objects.validation.schema.Person
import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.validate

class ClassRoomInput : Input {
    private val _people: MutableList<Person> = mutableListOf()
    fun person(person: PersonInput): ClassRoomInput {
        _people.add(person.value())
        return this
    }

    override fun value(): ClassRoom = ClassRoom(_people)

    override fun tryValidate(): Evaluation = value().validate()
}
