package store.domain

data class Store(
    val id: Int,
    val code: String?,
    val description: String?,
    val name: String?,
    val openingDate: String?,
    val storeType: String?,
)