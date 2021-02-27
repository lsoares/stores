package store.storeprovider

import io.javalin.Javalin
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import store.storeprovider.StoreProviderClient.StoreExtraFields

class ApiClientExtraFieldsTest {

    private lateinit var fakeApi: Javalin

    @AfterEach
    fun `after each`() {
        fakeApi.stop()
    }

    @Test
    fun `gets the special fields`() {
        var usedApiKey: String? = null
        fakeApi = Javalin.create().get("extra_data.csv") {
            usedApiKey = it.header("apiKey")
            it.result(
                """
                    Store id,Special field 1, Special field 2
                    1,     a      ,b
                    4,,
                    29279500,"a, b, c",2
                """.trimIndent()
            )
        }.start(1234)
        val storeGateway = StoreProviderClient(baseUrl = "http://localhost:1234", apiKey = "api-key1")

        val result = storeGateway.listSpecialFields()

        assertEquals("api-key1", usedApiKey)
        assertEquals(
            listOf(
                StoreExtraFields(
                    storeId = "1",
                    extraFields = mapOf(
                        "Special field 1" to "a",
                        "Special field 2" to "b",
                    ),
                ),
                StoreExtraFields(
                    storeId = "4",
                    extraFields = mapOf(
                        "Special field 1" to "",
                        "Special field 2" to "",
                    ),
                ),
                StoreExtraFields(
                    storeId = "29279500",
                    extraFields = mapOf(
                        "Special field 1" to "a, b, c",
                        "Special field 2" to "2",
                    ),
                ),
            ),
            result
        )
    }
}