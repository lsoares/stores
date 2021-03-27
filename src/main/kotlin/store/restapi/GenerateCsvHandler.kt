package store.restapi

import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import io.javalin.http.Context
import io.javalin.http.Handler
import store.domain.ListStoresUseCase

class GenerateCsvHandler(private val listStores: ListStoresUseCase) : Handler {

    override fun handle(ctx: Context) {
        ctx.contentType("application/force-download")
            .header("Content-Transfer-Encoding", "binary")
            .header("Content-Disposition", """attachment; filename="stores.csv"""")

        csvWriter().writeAll(
            listOf(listOf("Id.", "Type", "Name", "Code", "Opening date", "Seasons", "Extra fields", "Description"))
                    +
                    listStores().map {
                        listOf(
                            it.externalId,
                            it.type,
                            it.name,
                            it.code,
                            it.openingDate,
                            it.seasons,
                            it.extraFields, // TODO: separate fields by column
                            it.description,
                        )
                    },
            ctx.res.outputStream
        )
    }
}