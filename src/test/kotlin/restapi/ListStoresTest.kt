package restapi

import domain.ListStoresUseCase
import domain.Store
import io.javalin.Javalin
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.eclipse.jetty.http.HttpStatus
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.skyscreamer.jsonassert.JSONAssert
import java.net.URI
import java.net.http.HttpClient.newHttpClient
import java.net.http.HttpRequest.newBuilder
import java.net.http.HttpResponse.BodyHandlers.ofString

class ListStoresTest {

    private lateinit var server: Javalin
    private lateinit var listStores: ListStoresUseCase

    @BeforeAll
    @Suppress("unused")
    fun setup() {
        listStores = mockk()
        server = Javalin.create()
            .get("/", ListStoresHandler(listStores))
            .start(1234)
    }

    @AfterEach
    fun `after each`() = clearAllMocks()

    @AfterAll
    @Suppress("unused")
    fun `tear down`() {
        server.stop()
    }

    @Test
    fun `it serializes a list of stores`() {
        every { listStores() } returns listOf(
            Store(
                id = 1234,
                name = "Store 1",
            )
        )

        val response = newHttpClient().send(
            newBuilder().GET().uri(URI("http://localhost:1234")).build(), ofString()
        )

        assertEquals(HttpStatus.OK_200, response.statusCode())
        JSONAssert.assertEquals(
            """ [ { "id": 1234, "name": "Store 1" } ] """,
            response.body(),
            true
        )
    }
}