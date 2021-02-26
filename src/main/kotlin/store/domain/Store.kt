package store.domain

data class Store(
    val id: String,
    val code: String?,
    val description: String?,
    val name: String?,
    val openingDate: String?, // TODO: let's be lean and use String
    val type: String?,
)