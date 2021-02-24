package restapi

import domain.ListStoresUseCase
import domain.Store
import io.javalin.http.Context
import io.javalin.http.Handler

class ListStoresHandler(private val listStores: ListStoresUseCase) : Handler {
    override fun handle(ctx: Context) {
        ctx.json(listStores().toRepresenter())
    }

    private fun List<Store>.toRepresenter() =
        map { StoreRepresenter(it.id, it.name) }

    @Suppress("unused")
    private class StoreRepresenter(val id: Int, val name: String)
}
