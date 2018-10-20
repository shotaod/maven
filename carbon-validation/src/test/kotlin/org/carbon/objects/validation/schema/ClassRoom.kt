package org.carbon.objects.validation.schema

import org.carbon.objects.validation.Definition
import org.carbon.objects.validation.Validated
import org.carbon.objects.validation.invalidate


data class ClassRoom(
        val persons: List<Person>
) : Validated<ClassRoom> by ClassRoomSchema

object ClassRoomSchema : Validated<ClassRoom> {
    override val def: Definition<ClassRoom> = { clazz ->
        clazz.persons.shouldEachValidated() otherwise "persons".invalidate()
    }
}