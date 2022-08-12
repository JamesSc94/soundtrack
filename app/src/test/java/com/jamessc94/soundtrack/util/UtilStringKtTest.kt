package com.jamessc94.soundtrack.util

import junit.framework.TestCase
import org.junit.Test

class UtilStringKtTest : TestCase() {

    val links =
        arrayOf("youtube.com/v/vidid",
            "youtube.com/vi/vidid",
            "youtube.com/?v=vidid",
            "youtube.com/?vi=vidid",
            "youtube.com/watch?v=vidid",
            "youtube.com/watch?vi=vidid",
            "youtu.be/vidid",
            "youtube.com/embed/vidid",
            "youtube.com/embed/vidid",
            "www.youtube.com/v/vidid",
            "http://www.youtube.com/v/vidid",
            "https://www.youtube.com/v/vidid",
            "youtube.com/watch?v=vidid&wtv=wtv",
            "http://www.youtube.com/watch?dev=inprogress&v=vidid&feature=related",
            "https://m.youtube.com/watch?v=vidid")

    @Test
    fun testYoutubeLink(){
        val id = "vidid"

        assertEquals(id, "youtube.com/v/vidid".getYoutubeID())
        assertEquals(id, "youtube.com/vi/vidid".getYoutubeID())
        assertEquals(id, "www.youtube.com/v/vidid".getYoutubeID())
        assertEquals(id, "http://www.youtube.com/watch?dev=inprogress&v=vidid&feature=related".getYoutubeID())
        assertEquals(id, "https://m.youtube.com/watch?v=vidid".getYoutubeID())

    }


}