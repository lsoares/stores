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
import java.text.SimpleDateFormat

class ListStoresTest {

    @Test
    fun `list stores through REST API`() {
        var requestedPage: Int? = null
        val fakeDeps = object : AppConfig() {
            override val storesRepository = object : StoreRepository {
                override fun list(page: Int?, nameSearch: String?): List<Store> {
                    requestedPage = page
                    return listOf(Store(
                        id = "id1",
                        externalId = "1234",
                        name = "Store 1",
                        description = "desc 1",
                        code = "code 1",
                        openingDate = SimpleDateFormat("yyyy-MM-dd").parse("2000-12-31"),
                        type = "STORE TYPE 1",
                        extraFields = mapOf("ab" to "x", "xy" to "bla"),
                        seasons = setOf("H2 19", "H1 20", "H2 20", "H1 21"),
                    ))
                }

                override fun saveInfo(storeInfo: StoreInfo) = fail("no need to save")
                override fun saveExtraField(storeId: String, name: String, value: String) = fail("no need to save")
                override fun saveSeasons(storeId: String, seasons: Set<String>) = fail("no need to save")
                override fun setCustomStoreName(storeId: String, newName: String): Unit = fail("no need to save")
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
                         "openingDate": "2000-12-31",
                         "type": "Store type 1",
                         "extraFields": {
                            "ab": "x", 
                            "xy": "bla"
                         },
                         "seasons": ["2019◑", "2020", "2021◐"]
                       }
                    ] """,
                response.body(),
                true
            )
        }
    }
}