package org.carbon.objects.validation

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer
import com.fasterxml.jackson.databind.util.StdConverter
import io.kotlintest.matchers.types.shouldBeTypeOf
import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.evaluation.Key
import org.carbon.objects.validation.evaluation.source.Code
import org.carbon.objects.validation.input.ClassRoomInput
import org.junit.jupiter.api.Test

class JsonSerializeTest {

    private val objectMapper = ObjectMapper().apply {
        registerModule(CarbonValidationModule)
    }

    @Test
    fun serialize_violation() {
        val validation = ClassRoomInput()
            .person {
                name("s")
                password("illegal")
                portfolioUrl("not:a:url")
            }
            .person {
                emails("john@carbon.org", "c@rbon")
                onetimePw(45678)
            }
            .tryValidate()
        validation.shouldBeTypeOf<Evaluation.Rejected>()
        val rejected = validation as Evaluation.Rejected

        val json = objectMapper
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(rejected.entries)

        println(json)
    }
}

// some helpers
object CodeConverter : StdConverter<Code, String>() {
    override fun convert(value: Code?): String? = value?.canonicalName
}

object KeyConverter : StdConverter<Key, String>() {
    override fun convert(value: Key?): String? = value?.qualifiedName
}

object CarbonValidationModule : SimpleModule() {
    init {
        addSerializer(Code::class.java, StdDelegatingSerializer(CodeConverter))
        addSerializer(Key::class.java, StdDelegatingSerializer(KeyConverter))
    }
}