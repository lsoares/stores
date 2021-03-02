package store.storeprovider

import io.javalin.Javalin
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ApiClientListSeasonsTest {

    private lateinit var fakeApi: Javalin

    @AfterEach
    fun `after each`() {
        fakeApi.stop()
    }

    @Test
    fun `gets the stores seasons`() {
        var usedApiKey: String? = null
        fakeApi = Javalin.create().get("other/stores_and_seasons") {
            usedApiKey = it.header("apiKey")
            it.result("""[
                { "storeId": 1, "season": "H1 21" },
                { "storeId": 1, "season": "H2 22" },
                { "storeId": 2, "season": "H1 23" }
            ]""").contentType("application/json")
        }.start(1234)
        val storeGateway = StoreProviderClient(baseUrl = "http://localhost:1234", apiKey = "api-key1")

        val result = storeGateway.listSeasons()

        assertEquals("api-key1", usedApiKey)
        assertEquals(
            mapOf(
                "1" to setOf("H1 21", "H2 22"),
                "2" to setOf("H1 23"),
            ),
            result
        )
    }

    @Test
    fun `returns error when not 200`() {
        fakeApi = Javalin.create().get("extra_data.csv") {
            it.status(500)
        }.start(1234)
        val storeGateway = StoreProviderClient(baseUrl = "http://localhost:1234", apiKey = "api-key1")

        val result = { storeGateway.listSeasons() }

        assertThrows<Exception> { result() }
    }
}