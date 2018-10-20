package org.carbon.objects.validation.input

import org.carbon.objects.validation.evaluation.Evaluation

interface Input {
    fun value(): Any
    fun tryValidate(): Evaluation
}