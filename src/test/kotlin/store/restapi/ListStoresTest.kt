package store.restapi

import org.eclipse.jetty.http.HttpStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.skyscreamer.jsonassert.JSONAssert
import store.AppConfig
import store.domain.Store
import store.domain.StoreInfo
import store.domain.StoreRepository
import java.net.URI
import java.net.http.HttpClient.newHttpClient
import java.net.http.HttpRequest.newBuilder
import java.net.http.HttpResponse.BodyHandlers.ofString

class ListStoresTest {

    @Test
    fun `list stores through REST API`() {
        var requestedPage: Int? = null
        val fakeDeps = object : AppConfig() {
            override val storesRepository = object : StoreRepository {
                override fun list(page: Int): List<Store> {
                    requestedPage = page
                    return listOf(Store(
                        id = "1234",
                        name = "Store 1",
                        description = "desc 1",
                        code = "code 1",
                        openingDate = "date 1",
                        type = "STORE TYPE 1",
                        extraFields = mapOf("ab" to "x", "xy" to "bla")
                    ))
                }

                override fun saveInfo(storeInfo: StoreInfo) = fail("no need to save")
                override fun saveExtraField(storeId: String, name: String, value: String) = fail("no need to save")
            }
        }
        App(fakeDeps).start(1234).use {
            val response = newHttpClient().send(
                newBuilder().GET().uri(URI("http://localhost:1234/stores?page=12")).build(), ofString()
            )

            assertEquals(11, requestedPage)
            assertEquals(HttpStatus.OK_200, response.statusCode())
            JSONAssert.assertEquals(
                """ [
                       { 
                         "id": "1234",
                         "name": "Store 1",
                         "description": "desc 1",
                         "code": "code 1",
                         "openingDate": "date 1",
                         "type": "Store type 1",
                         "extraFields": {
                            "ab": "x", 
                            "xy": "bla"
                         }
                       }
                    ] """.trimMargin(),
                response.body(),
                true
            )
        }
    }
}