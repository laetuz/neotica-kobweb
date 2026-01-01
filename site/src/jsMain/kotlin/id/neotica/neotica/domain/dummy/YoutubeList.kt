package id.neotica.neotica.domain.dummy

import id.neotica.neotica.domain.model.YoutubeVideo
import id.neotica.neotica.utils.toEpoch
import kotlin.js.Date

class YoutubeList {
    val videoList = mutableListOf(
        YoutubeVideo(
            "Android 2.2 Froyo Official Video",
            "Highlights from the latest Android platform release\n" +
                    "\n" +
                    "⭐Clue #5 ⭐\n" +
                    "\n" +
                    "You got clue number five and if you’ve still got the bandwidth, try to find the next clue in our video for Ice Cream Sandwich. \uD83D\uDE0B",
            Date(2010, 4, 20).toEpoch(),
            "https://photos.app.goo.gl/2wVFoaBhRBrCEoSB8",
            "yAZYSVr2Bhc"
        ),
        YoutubeVideo(
            "Android Market (1.6)",
            "Find cool applications and games for your phone on Android Market.  Please note that this video is for devices running Android 1.6",
            Date(2009, 8, 24).toEpoch(),
            "https://photos.app.goo.gl/NSnjAUovHHuXtxS36",
            "fSRlUzUGlfo"
        ),
        YoutubeVideo(
            title = "Home Screen (1.5 and 1.6)",
            description = "Learn how to take advantage of the Home Screen's features on your Android-powered phone.  Please note that this video is for devices running Android 1.5 and 1.6",
            dateUploaded = Date(2009, 8, 24).toEpoch(),
            url = "https://photos.app.goo.gl/nrD7zQt6BfcmNvMg8",
            videoId = "2oWa8Rpo9jQ"
        ),
        YoutubeVideo(
            title = "Running Multiple Apps (1.5 and 1.6)",
            description = "Run multiple apps on your Android-powered phone.  Please note that this video is for devices running Android 1.5 and 1.6",
            dateUploaded = Date(2009, 8, 24).toEpoch(),
            url = "https://photos.app.goo.gl/WPEyc5ik8FQnUGez7",
            videoId = "QvpsquvdqVk"
        ),
        YoutubeVideo(
            title = "Long Press (1.5 and 1.6)",
            description = "Long Press is a nifty feature that reveals additional functionality on your Android-powered phone.  Please note that this video is for devices running Android 1.5 and 1.6",
            dateUploaded = Date(2009, 8, 24).toEpoch(),
            url = "https://photos.app.goo.gl/HqX2fAif8KYckg65A",
            videoId = "rQmVKgPikiU"
        ),
        YoutubeVideo(
            title = "Quick Search Box (1.6)",
            description = "Easily search both your phone and the web with Quick Search Box.  Please note that this video is for devices running Android 1.6",
            dateUploaded = Date(2009, 8, 24).toEpoch(),
            url = "https://photos.app.goo.gl/i2NxnseN6HaFryo4A",
            videoId = "fXpJoLqb5VA"
        ),
    )
}