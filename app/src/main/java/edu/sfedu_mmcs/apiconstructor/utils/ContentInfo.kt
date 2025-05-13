package edu.sfedu_mmcs.apiconstructor.utils

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContentInfo(
    val name: String,
    val example: String,
    val format: String? = null,
    val enumValues: List<String>? = null,
    val required: Boolean = false
) : Parcelable