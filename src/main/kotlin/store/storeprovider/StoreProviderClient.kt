package store.storeprovider

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import org.eclipse.jetty.http.HttpStatus
import store.domain.Store
import store.domain.StoreRepository
import java.lang.RuntimeException
import java.net.URI
import java.net.http.HttpClient.newHttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers.ofString

class StoreProviderClient(private val baseUrl: String, private val apiKey: String): StoreRepository {

    override fun list(page: Int): List<Store> {
        val httpRequest = HttpRequest.newBuilder()
            .uri(URI.create("$baseUrl/v1/stores/?page=$page"))
            .header("apiKey", apiKey)
            .GET()

        return newHttpClient.send(httpRequest.build(), ofString()).run {
            check(statusCode() == HttpStatus.OK_200) {
                throw RuntimeException("${statusCode()} - ${body()}")
            }
            body().toStore()
        }
    }

    private fun String.toStore() =
        (objectMapper.readTree(this) as ArrayNode).map {
            Store(
                id = it.get("id").asInt(),
                code = it.get("code").textValue(),
                description = it.get("description").textValue(),
                name = it.get("name").textValue(),
                openingDate = it.get("openingDate").textValue(),
                type = it.get("storeType").textValue(),
            )
        }
}

private val objectMapper = ObjectMapper()
private val newHttpClient = newHttpClient()