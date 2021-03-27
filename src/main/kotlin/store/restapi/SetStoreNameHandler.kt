package store.restapi

import io.javalin.http.Context
import io.javalin.http.Handler
import org.eclipse.jetty.http.HttpStatus
import store.domain.SetStoreNameUseCase

class SetStoreNameHandler(val setStoreNameUseCase: SetStoreNameUseCase) : Handler {

    override fun handle(ctx: Context) {
        val storeId = ctx.pathParam("id")
        setStoreNameUseCase(
            storeId,
            ctx.body<NewNameRepresenter>().newName
        )
        ctx.status(HttpStatus.NO_CONTENT_204)
    }

    private data class NewNameRepresenter(val newName: String)
}