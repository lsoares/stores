package store.domain

import java.util.*

data class Store(
    val id: String,
    val code: String?,
    val description: String?,
    val name: String?,
    val openingDate: Date?,
    val type: String?,
    val extraFields: Map<String, String?> = emptyMap(),
    val seasons: Set<String> = emptySet(),
)

data class StoreInfo(
    val id: String,
    val code: String? = null,
    val description: String? = null,
    val name: String? = null,
    val openingDate: Date? = null,
    val type: String? = null,
)
