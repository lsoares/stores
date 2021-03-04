package store.restapi

import io.javalin.http.Context
import io.javalin.http.Handler
import store.domain.ListStoresUseCase
import store.domain.Store
import java.text.SimpleDateFormat
import java.util.*

class ListStoresHandler(private val listStores: ListStoresUseCase) : Handler {

    override fun handle(ctx: Context) {
        ctx.json(listStores(ctx.page, ctx.textSearch).toRepresenter())
    }

    private val Context.textSearch
        get() = queryParam("nameSearch").takeUnless(String?::isNullOrBlank)

    private val Context.page
        get() = queryParam("page")?.toInt()?.minus(1) ?: error("missing page param")

    private fun List<Store>.toRepresenter() =
        map {
            StoreRepresenter(
                id = it.externalId,
                name = it.name,
                code = it.code,
                description = it.description,
                openingDate = it.openingDate,
                type = it.type,
                extraFields = it.extraFields,
                seasons = it.seasons,
            )
        }

    @Suppress("unused")
    private class StoreRepresenter(
        val id: String,
        val name: String?,
        val code: String?,
        val description: String?,
        val extraFields: Map<String, String?>,
        openingDate: Date?,
        seasons: Set<String>,
        type: String?,
    ) {
        val type = type?.toLowerCase()?.capitalize()
        val openingDate: String? = openingDate?.let { dateFormat.format(it) }
        val seasons: List<String> = seasons.groupBy { it.takeLast(2) }
            .mapKeys { it.key.toIntOrNull()?.plus(2000) }
            .mapValues {
                if (it.value.size == 1) {
                    when {
                        it.value.first().startsWith("H1") -> "◐"
                        it.value.first().startsWith("H2") -> "◑"
                        else -> ""
                    }
                } else ""
            }.map { "${it.key}${it.value}" }
    }
}

private val dateFormat = SimpleDateFormat("yyyy-MM-dd")
