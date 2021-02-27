package store.domain

data class Store(
    val id: String,
    val code: String?,
    val description: String?,
    val name: String?,
    val openingDate: String?,
    val type: String?,
    val extraFields: Map<String, String?> = emptyMap(),
    val seasons: Set<String> = emptySet(),
)

data class StoreInfo(
    val id: String,
    val code: String?,
    val description: String?,
    val name: String?,
    val openingDate: String?,
    val type: String?,
)

