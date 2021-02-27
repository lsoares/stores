package store.restapi

import io.javalin.http.Context
import io.javalin.http.Handler
import store.domain.ListStoresUseCase
import store.domain.Store

class ListStoresHandler(private val listStores: ListStoresUseCase) : Handler {

    override fun handle(ctx: Context) {
        ctx.json(listStores(ctx.page, ctx.textSearch).toRepresenter())
    }

    private val Context.textSearch
        get() = queryParam("nameSearch").takeUnless { it.isNullOrBlank() }

    private val Context.page
        get() = queryParam("page")?.toInt()?.minus(1) ?: error("missing page param")

    private fun List<Store>.toRepresenter() =
        map {
            StoreRepresenter(
                id = it.id,
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
        val openingDate: String?,
        val extraFields: Map<String, String?>,
        val seasons: Set<String>, // TODO improve string representation
        type: String?,
    ) {
        val type = type?.toLowerCase()?.capitalize()
    }
}
