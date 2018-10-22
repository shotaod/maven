package org.carbon.objects.validation.schema

import org.carbon.objects.validation.Definition
import org.carbon.objects.validation.Validated
import org.carbon.objects.validation.invalidate
import org.carbon.objects.validation.matcher.IncludeShape.AllOf
import org.carbon.objects.validation.matcher.Natural
import org.carbon.objects.validation.matcher.URL
import org.carbon.objects.validation.matcher.be
import org.carbon.objects.validation.matcher.include

data class Resume(
        val portfolioUrl: String,
        val income: Int,
        val certifications: List<String>,
        val text: String
) : Validated<Resume> by ResumeSchema

object ResumeSchema : Validated<Resume> {
    override val def: Definition<Resume> = { resume ->

        resume.portfolioUrl should { it be URL } otherwise "portfolioUrl".invalidate()

        resume.income should { it be Natural } otherwise "income".invalidate()

        resume.text should { it include AllOf(*resume.certifications.toTypedArray()) } otherwise "text".invalidate()
    }
}
