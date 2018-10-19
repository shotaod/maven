package org.carbon.objects.validation


data class ClassRoom(
        val persons: List<Person>
) : Validated<ClassRoom> by ClassRoomSchema

object ClassRoomSchema : Validated<ClassRoom> {
    override val def: Definition<ClassRoom> = { clazz ->
        clazz.persons.shouldEachValidated() otherwise "persons".invalidate()
    }
}