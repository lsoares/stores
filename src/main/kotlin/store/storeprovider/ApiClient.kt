package store.storeprovider

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import org.eclipse.jetty.http.HttpStatus
import store.domain.Store
import java.net.URI
import java.net.http.HttpClient.newHttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers.ofString

class StoreProviderClient(private val baseUrl: String, private val apiKey: String) {

    fun listStores(page: Int): ListStoresResult {
        val httpRequest = HttpRequest.newBuilder()
            .uri(URI.create("$baseUrl/v1/stores/?page=$page"))
            .header("apiKey", apiKey)
            .GET()

        return httpClient.send(httpRequest.build(), ofString()).run {
            if (statusCode() != HttpStatus.OK_200) {
                return ListStoresResult.FailedToFetch
            }
            ListStoresResult.Valid(body().toStore())
        }
    }

    sealed class ListStoresResult {
        class Valid(val stores: List<Store>) : ListStoresResult()
        object FailedToFetch : ListStoresResult()
    }

    private fun String.toStore() =
        (objectMapper.readTree(this) as ArrayNode).map {
            Store(
                id = it.get("id").intValue().toString(),
                code = it.get("code").textValue()?.trim(),
                description = it.get("description").textValue(),
                name = it.get("name").textValue(),
                openingDate = it.get("openingDate").textValue(),
                type = it.get("storeType").textValue(),
            )
        }

    fun listSpecialFields(): List<SpecialFields> {
        val httpRequest = HttpRequest.newBuilder()
            .uri(URI.create("$baseUrl/extra_data.csv"))
            .header("apiKey", apiKey)
            .GET()

        return httpClient.send(httpRequest.build(), ofString()).run {
            csvReader().readAllWithHeader(body().trim())
                .mapNotNull { it ->
                    val row = it.mapKeys { it.key.trim() }

                    SpecialFields(
                        storeId = row["Store id"] ?: return@mapNotNull null,
                        properties = row.filterNot { it.key == "Store id" }
                    )
                }
        }
    }

    data class SpecialFields(val storeId: String, val properties: Map<String, String>)
}

private val objectMapper = ObjectMapper()
private val httpClient = newHttpClient()