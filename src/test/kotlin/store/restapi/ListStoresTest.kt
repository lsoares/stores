package store.restapi

import org.eclipse.jetty.http.HttpStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import store.domain.Store
import store.domain.StoreRepository
import java.net.URI
import java.net.http.HttpClient.newHttpClient
import java.net.http.HttpRequest.newBuilder
import java.net.http.HttpResponse.BodyHandlers.ofString

class ListStoresTest {

    @Test
    fun `list stores though REST API`() {
        val fakeDeps = object : Dependencies() {
            override val storesRepository = object : StoreRepository {
                override fun list() =
                    listOf(Store(id = 1234, name = "Store 1"))
            }
        }
        WebApp(fakeDeps).use {
            it.start(1234)

            val response = newHttpClient().send(
                newBuilder().GET().uri(URI("http://localhost:1234/stores")).build(), ofString()
            )

            assertEquals(HttpStatus.OK_200, response.statusCode())
            JSONAssert.assertEquals(
                """ [ { "id": 1234, "name": "Store 1" } ] """,
                response.body(),
                true
            )
        }
    }
}