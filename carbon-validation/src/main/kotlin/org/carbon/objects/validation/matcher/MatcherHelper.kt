package org.carbon.objects.validation.matcher

import org.carbon.objects.validation.Logical
import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.evaluation.Key
import org.carbon.objects.validation.evaluation.rejection.CompositeRejection
import org.carbon.objects.validation.evaluation.rejection.UnitRejection
import org.carbon.objects.validation.evaluation.source.Code
import org.carbon.objects.validation.evaluation.source.ParamList
import org.carbon.objects.validation.evaluation.source.Source

fun <T : Any> T.reject(
        code: Code,
        params: ParamList<*>,
        defaultMessage: String? = ""
): Evaluation.Rejection<T> = UnitRejection(
        Key.ShouldBeResolved,
        this,
        Source(code, params, defaultMessage)
)

fun <T : Any> T.reject(
        vararg rejection: Evaluation.Rejection<T>,
        logical: Logical
): Evaluation.Rejection<T> = CompositeRejection(
        this,
        logical,
        rejection.toList()
)