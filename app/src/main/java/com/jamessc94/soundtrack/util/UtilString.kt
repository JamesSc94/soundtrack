package com.jamessc94.soundtrack.util

import java.util.regex.Matcher
import java.util.regex.Pattern

fun String.getYoutubeID() : String {
    val videoIdRegex  = arrayOf("\\?vi?=([^&]*)","watch\\?.*v=([^&]*)", "(?:embed|vi?)/([^/?]*)", "^([A-Za-z0-9\\-]*)")
    val temp = this.youTubeLinkWithoutProtocolAndDomain()

    for (regex in videoIdRegex) {
        val compiledPattern: Pattern = Pattern.compile(regex)
        val matcher: Matcher = compiledPattern.matcher(temp)
        if (matcher.find()) {
            return matcher.group(1)!!
        }
    }

    return ""

}

fun String.youTubeLinkWithoutProtocolAndDomain() : String {
    val compiledPattern = Pattern.compile("^(https?)?(://)?(www.)?(m.)?((youtube.com)|(youtu.be))/")
    val matcher = compiledPattern.matcher(this)

    return if (matcher.find()) {
        this.replace(matcher.group(), "")

    } else this
}