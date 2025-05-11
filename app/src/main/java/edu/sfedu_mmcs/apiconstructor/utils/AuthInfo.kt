package edu.sfedu_mmcs.apiconstructor.utils

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class AuthInfo(
    val name: String,
    val type: String,
    val value: String
): Parcelable