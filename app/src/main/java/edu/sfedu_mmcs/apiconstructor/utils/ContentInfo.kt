package edu.sfedu_mmcs.apiconstructor.utils

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ContentInfo(
    val name: String,
    val example: String,
): Parcelable {
}