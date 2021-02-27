package store.storeprovider

import io.javalin.Javalin
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import store.domain.Store.Season.FIRST_HALF
import store.domain.Store.Season.SECOND_HALF
import store.domain.StoreSeason

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
                { "storeId": 2, "season": "H1 23" },
                { "storeId": 2, "season": "X1 23" }
            ]""").contentType("application/json")
        }.start(1234)
        val storeGateway = StoreProviderClient(baseUrl = "http://localhost:1234", apiKey = "api-key1")

        val result = storeGateway.listSeasons()

        assertEquals("api-key1", usedApiKey)
        assertEquals(
            listOf(
                StoreSeason("1", 2021, FIRST_HALF),
                StoreSeason("1", 2022, SECOND_HALF),
                StoreSeason("2", 2023, FIRST_HALF),
            ),
            result
        )
    }
}