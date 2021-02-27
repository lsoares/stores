package store.domain

data class Store(
    val id: String,
    val code: String?,
    val description: String?,
    val name: String?,
    val openingDate: String?,
    val type: String?,
    val extraFields: Map<String, String?> = emptyMap(),
    val operationalDuring: Set<Pair<Int, Season>> = emptySet(),
) {
    enum class Season { FIRST_HALF, SECOND_HALF }
}

data class StoreInfo(
    val id: String,
    val code: String?,
    val description: String?,
    val name: String?,
    val openingDate: String?, // TODO: let's be lean and use String
    val type: String?,
)

data class StoreSeason(val storeId: String, val year: Int, val season: Store.Season)
