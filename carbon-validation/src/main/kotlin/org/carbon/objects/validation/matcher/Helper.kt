package org.carbon.objects.validation.matcher

import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.evaluation.Key
import org.carbon.objects.validation.evaluation.UnitRejection
import org.carbon.objects.validation.evaluation.source.Code
import org.carbon.objects.validation.evaluation.source.Param
import org.carbon.objects.validation.evaluation.source.Source

fun <T : Any> T.reject(
        code: Code,
        params: Param<*>,
        defaultMessage: String? = ""
): Evaluation.Rejection<T> = UnitRejection(
        Key.Unresolved,
        this,
        Source(code, params, defaultMessage)
)