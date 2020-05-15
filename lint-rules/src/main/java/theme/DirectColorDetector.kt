package com.bugtsa.casher.theme

import com.android.tools.lint.detector.api.*
import org.w3c.dom.Attr

class DirectColorDetector : ResourceXmlDetector() {
    override fun getApplicableAttributes(): Collection<String>? = listOf(
            "background", "foreground", "src", "textColor", "tint", "color",
            "textColorHighlight", "textColorHint", "textColorLink",
            "shadowColor", "srcCompat")

    override fun visitAttribute(context: XmlContext, attribute: Attr) {
        if (attribute.value.startsWith("#")) {
            context.report(
                    DIRECT_COLOR_ISSUE,
                    context.getLocation(attribute),
                    DIRECT_COLOR_ISSUE.getExplanation(TextFormat.RAW))
        }
    }
}

val DIRECT_COLOR_ISSUE = Issue.create("DirectColorUse",
        "Direct color used",
        "Avoid direct use of colors in XML files. This will cause issues with different theme (eg. night) support",
        Category.CORRECTNESS,
        6,
        Severity.ERROR,
        Implementation(DirectColorDetector::class.java, Scope.RESOURCE_FILE_SCOPE)
)