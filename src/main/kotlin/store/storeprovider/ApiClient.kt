package store.storeprovider

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import org.eclipse.jetty.http.HttpStatus
import store.domain.StoreInfo
import java.net.URI
import java.net.http.HttpClient.newHttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers.ofString
import java.text.SimpleDateFormat

class StoreProviderClient(private val baseUrl: String, private val apiKey: String) {

    fun listStores(page: Int): List<StoreInfo> {
        val httpRequest = HttpRequest.newBuilder()
            .uri(URI.create("$baseUrl/v1/stores/?page=$page"))
            .header("apiKey", apiKey)
            .GET()

        return httpClient.send(httpRequest.build(), ofString()).run {
            check(statusCode() == HttpStatus.OK_200)
            body().toStores()
        }
    }

    private fun String.toStores() =
        (objectMapper.readTree(this) as ArrayNode).map {
            StoreInfo(
                externalId = it.get("id").intValue().toString(),
                code = it.get("code")?.textValue()?.trim(),
                description = it.get("description")?.textValue(),
                name = it.get("name")?.textValue(),
                openingDate = it.get("openingDate")?.textValue()?.let {
                    SimpleDateFormat("yyyy-MM-dd").parse(it)
                },
                type = it.get("storeType")?.textValue(),
            )
        }

    fun listSpecialFields(): List<StoreExtraFields> {
        val httpRequest = HttpRequest.newBuilder()
            .uri(URI.create("$baseUrl/extra_data.csv"))
            .header("apiKey", apiKey)
            .GET()

        return httpClient.send(httpRequest.build(), ofString()).run {
            check(statusCode() == HttpStatus.OK_200)

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

    fun listSeasons(): Map<String, Set<String>> {
        val httpRequest = HttpRequest.newBuilder()
            .uri(URI.create("$baseUrl/other/stores_and_seasons"))
            .header("apiKey", apiKey)
            .GET()

        return httpClient.send(httpRequest.build(), ofString()).run {
            check(statusCode() == HttpStatus.OK_200)

            (objectMapper.readTree(body()) as ArrayNode).groupBy {
                it["storeId"].intValue().toString()
            }.mapValues {
                it.value.map {
                    it["season"].asText()
                }.toSet()
            }.toMap()
        }
    }

    data class StoreExtraFields(val storeId: String, val extraFields: Map<String, String>)
}

private val objectMapper = ObjectMapper()
private val httpClient = newHttpClient()