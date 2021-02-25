package store.storeprovider

import io.javalin.Javalin
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import store.storeprovider.StoreGateway.StoreInfo

class StoreGatewayTest {

    private lateinit var fakeApi: Javalin

    @AfterEach
    fun `after each`() {
        fakeApi.stop()
    }

    @Test
    fun `gets the list of stores`() {
        var usedApiKey: String? = null
        var usedPage: Int? = null
        fakeApi = Javalin.create().get("v1/stores") {
            usedApiKey = it.header("apiKey")
            usedPage = it.queryParam("page")?.toInt()
            it.result(
                """[
              {
                "id": 101,
                "code": "code1",
                "description": null,
                "name": "store 1",
                "openingDate": "2021-02-07",
                "storeType": "RETAIL"
              },
              {
                "id": 102,
                "code": "code2",
                "description": "desc 2",
                "name": "store 2",
                "openingDate": "2019-01-03",
                "storeType": null
              }
            ]"""
            )
            it.contentType("application/json")
        }.start(1234)
        val storeGateway = StoreGateway(baseUrl = "http://localhost:1234", apiKey = "api-key1")

        val result = storeGateway.listStores(2)

        assertEquals("api-key1", usedApiKey)
        assertEquals(2, usedPage)
        assertEquals(
            listOf(
                StoreInfo(
                    id = 101,
                    name = "store 1",
                    description = null,
                    code = "code1",
                    openingDate = "2021-02-07",
                    storeType = "RETAIL",
                ),
                StoreInfo(
                    id = 102,
                    name = "store 2",
                    description = "desc 2",
                    code = "code2",
                    openingDate = "2019-01-03",
                    storeType = null,
                ),
            ),
            result
        )
    }
}