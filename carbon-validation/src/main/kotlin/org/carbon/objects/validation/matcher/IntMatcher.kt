package org.carbon.objects.validation.matcher

import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.evaluation.reject
import org.carbon.objects.validation.evaluation.source.BasicViolationSource
import org.carbon.objects.validation.evaluation.source.NumberViolationSource

object IntMatcher : Matcher<Int> {
    // -----------------------------------------------------
    //                                               Infix
    //                                               -------
    override fun Int.eq(other: Int): Evaluation =
            if (this == other) Evaluation.Accept
            else this.reject(BasicViolationSource.Equal(this, other), "not match")

    infix fun Int.range(range: IntRange): Evaluation {
        val min = range.first
        val max = range.last
        if (min > max)
            throw IllegalArgumentException("min and max should be max > min")

        return if (this in min..max) Evaluation.Accept
        else this.reject(NumberViolationSource.range(range), "number must be between $min and $max")
    }

    infix fun Int.min(min: Int): Evaluation {
        if (min < 0) throw IllegalArgumentException("min should be greater than 0")
        return if (this >= min) Evaluation.Accept
        else this.reject(NumberViolationSource.min(min), "number must be less than $min")
    }

    infix fun Int.max(max: Int): Evaluation {
        if (max < 0) throw IllegalArgumentException("max should be greater than 0")
        return if (this <= max) Evaluation.Accept
        else this.reject(NumberViolationSource.max(max), "number must be greater than $max")
    }
}
