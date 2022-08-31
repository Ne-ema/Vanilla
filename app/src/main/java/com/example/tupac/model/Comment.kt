package com.example.tupac.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.w3c.dom.Comment

@Parcelize
data class Comment(
    var username : String? = null,
    var content: String? = null,
    var uid: String? = null,
    var postKey: Long? = null,
    var commentTime:Long? = null,
):Parcelable