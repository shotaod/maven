package org.carbon.objects.validation.matcher

import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.evaluation.Key
import org.carbon.objects.validation.evaluation.rejection.UnitRejection
import org.carbon.objects.validation.evaluation.source.Code
import org.carbon.objects.validation.evaluation.source.ParamList
import org.carbon.objects.validation.evaluation.source.Source

fun <T : Any> T.reject(
        code: Code,
        params: ParamList<*>,
        defaultMessage: String? = ""
): Evaluation.Rejected = Evaluation.Rejected.from(
        UnitRejection(
                Key.Undefined,
                this,
                Source(code, params, defaultMessage)
        ))
