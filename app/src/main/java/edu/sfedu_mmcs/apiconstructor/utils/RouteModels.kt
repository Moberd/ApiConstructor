package edu.sfedu_mmcs.apiconstructor.utils
import edu.sfedu_mmcs.apiconstructor.utils.RouteInfo

data class RouteGroup(
    val groupName: String,
    val endpoints: List<Endpoint>,
    var isExpanded: Boolean = false
)

data class Endpoint(
    val path: String,
    val methods: List<MethodItem>,
    var isExpanded: Boolean = false,
    val isSingleMethod: Boolean = methods.size == 1
)

data class MethodItem(
    val routeInfo: RouteInfo,
    val method: String
)