package edu.sfedu_mmcs.apiconstructor.utils
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class RouteGroup(
    val groupName: String,
    val endpoints: List<Endpoint>,
    var isExpanded: Boolean = false
)

data class Endpoint(
    val path: String,
    val methods: List<RouteInfo>,
    var isExpanded: Boolean = false,
    val isSingleMethod: Boolean = methods.size == 1
)

@Parcelize
data class RouteInfo(
    val route: String,
    val method: String,
    val type: String,
    val fields: ArrayList<ContentInfo>,
    val content: ArrayList<ContentInfo>,
    val security: ArrayList<String>,
    val responses: HashMap<String, String>,
    val description: String
): Parcelable