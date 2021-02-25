package store.storeprovider

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import org.eclipse.jetty.http.HttpStatus
import java.net.URI
import java.net.http.HttpClient.newHttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers.ofString

class StoreProviderClient(private val baseUrl: String, private val apiKey: String) {

    fun listStores(page: Int): List<StoreInfo> {
        val httpRequest = HttpRequest.newBuilder()
            .uri(URI.create("$baseUrl/v1/stores?page=$page"))
            .header("apiKey", apiKey)
            .GET()

        return newHttpClient.send(httpRequest.build(), ofString()).run {
            check(statusCode() == HttpStatus.OK_200)
            body().toStoreInfo()
        }
    }

    private fun String.toStoreInfo() =
        (objectMapper.readTree(this) as ArrayNode).map {
            StoreInfo(
                id = it.get("id").asInt(),
                code = it.get("code").textValue(),
                description = it.get("description").textValue(),
                name = it.get("name").textValue(),
                openingDate = it.get("openingDate").textValue(),
                storeType = it.get("storeType").textValue(),
            )
        }

    data class StoreInfo(
        val id: Int,
        val code: String?,
        val description: String?,
        val name: String?,
        val openingDate: String,
        val storeType: String?,
    )
}

private val objectMapper = ObjectMapper()
private val newHttpClient = newHttpClient()