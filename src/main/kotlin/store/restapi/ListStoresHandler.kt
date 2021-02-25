package store.restapi

import io.javalin.http.Context
import io.javalin.http.Handler
import store.domain.ListStoresUseCase
import store.domain.Store

class ListStoresHandler(private val listStores: ListStoresUseCase) : Handler {

    override fun handle(ctx: Context) {
        ctx.json(listStores(ctx.page).toRepresenter())
    }

    private val Context.page get() = queryParam("page")?.toInt() ?: error("missing page param")

    private fun List<Store>.toRepresenter() =
        map { StoreRepresenter(id = it.id, name = it.name) }

    @Suppress("unused")
    private class StoreRepresenter(val id: Int, val name: String?)
}
