package edu.sfedu_mmcs.apiconstructor.utils

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class RouteInfo(
    val route: String,
    val method: String,
    val type: String,
    val fields: ArrayList<ContentInfo>,
    val content: ArrayList<ContentInfo>,
    val security: ArrayList<String>,
    val responses: HashMap<String, String>,
    val description: String
): Parcelable