package store.restapi

import org.eclipse.jetty.http.HttpStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import store.AppConfig
import store.domain.StoreInfo
import store.domain.StoreRepository
import java.net.URI
import java.net.http.HttpClient.newHttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers.ofString
import java.net.http.HttpResponse.BodyHandlers.ofString

class SetStoreNameTest {

    @Test
    fun `set store name through REST API`() {
        var savedNewName: String? = null
        var usedStoreId: String? = null
        val fakeDeps = object : AppConfig() {
            override val storesRepository = object : StoreRepository {
                override fun findById(storeId: String) = fail("no need to find")
                override fun list(page: Int, nameSearch: String?) = fail("no need to list")
                override fun saveInfo(storeInfo: StoreInfo) = fail("no need to save")
                override fun saveExtraField(storeId: String, name: String, value: String) = fail("no need to save")
                override fun saveSeasons(storeId: String, seasons: Set<String>) = fail("no need to save")
                override fun updateStoreName(storeId: String, newName: String) {
                    usedStoreId = storeId
                    savedNewName = newName
                }
            }
        }
        App(fakeDeps).start(1234).use {
            val response = newHttpClient().send(
                HttpRequest.newBuilder()
                    .method("PATCH", ofString(""" { "newName": "new store name" } """))
                    .uri(URI("http://localhost:1234/stores/4444"))
                    .build(),
                ofString()
            )

            assertEquals(HttpStatus.NO_CONTENT_204, response.statusCode())
            assertEquals("new store name", savedNewName)
            assertEquals("4444", usedStoreId)
        }
    }
}