package store.storeprovider

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import org.eclipse.jetty.http.HttpStatus
import store.domain.Store.Season.FIRST_HALF
import store.domain.Store.Season.SECOND_HALF
import store.domain.StoreInfo
import store.domain.StoreSeason
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
        class Valid(val storeInfos: List<StoreInfo>) : ListStoresResult()
        object FailedToFetch : ListStoresResult()
    }

    private fun String.toStore() =
        (objectMapper.readTree(this) as ArrayNode).map {
            StoreInfo(
                id = it.get("id").intValue().toString(),
                code = it.get("code").textValue()?.trim(),
                description = it.get("description").textValue(),
                name = it.get("name").textValue(),
                openingDate = it.get("openingDate").textValue(),
                type = it.get("storeType").textValue(),
            )
        }

    fun listSpecialFields(): List<StoreExtraFields> {
        val httpRequest = HttpRequest.newBuilder()
            .uri(URI.create("$baseUrl/extra_data.csv"))
            .header("apiKey", apiKey)
            .GET()

        return httpClient.send(httpRequest.build(), ofString()).run {
            csvReader().readAllWithHeader(body().trim())
                .mapNotNull { row ->
                    val trimmedRow = row.mapKeys { it.key.trim() }.mapValues { it.value.trim() }
                    StoreExtraFields(
                        storeId = trimmedRow["Store id"] ?: return@mapNotNull null,
                        extraFields = trimmedRow.filterNot { it.key == "Store id" }
                    )
                }
        }
    }

    fun listSeasons(): List<StoreSeason> {
        val httpRequest = HttpRequest.newBuilder()
            .uri(URI.create("$baseUrl/other/stores_and_seasons"))
            .header("apiKey", apiKey)
            .GET()

        return httpClient.send(httpRequest.build(), ofString()).run {
            (objectMapper.readTree(body()) as ArrayNode).mapNotNull {
                StoreSeason(
                    storeId = it["storeId"].intValue().toString(),
                    year = 2000 + it["season"].asText().takeLast(2).toInt(),
                    season = when (it["season"].asText().take(2)) {
                        "H1" -> FIRST_HALF
                        "H2" -> SECOND_HALF
                        else -> return@mapNotNull null
                    }
                )
            }
        }
    }

    data class StoreExtraFields(val storeId: String, val extraFields: Map<String, String>)
}

private val objectMapper = ObjectMapper()
private val httpClient = newHttpClient()