package store.cloudtolocal

import store.AppConfig
import store.RealConfig
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun main() {
    Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate({
        runCatching {
            RealConfig.importAllData()
        }.onFailure {
            println("🚨 Error in job")
            it.printStackTrace()
        }
    }, 0, 1, TimeUnit.HOURS)
}

private fun AppConfig.importAllData() {
    println("🏬 ------ Stores import job starting ${Date()} -------")
    println("Importing stores info…")
    importStoresInfo()
    println("Importing extra fields…")
    importExtraFields()
    println("Importing seasons…")
    importSeasons()
    println("✅ ${Date()}")
}

private fun AppConfig.importStoresInfo() {
    var page = 1
    do {
        print(" $page")
        val result = runCatching {
            storesProvider.listStores(page)
        }.onSuccess {
            it.map(storesRepository::saveInfo)
        }.onFailure {
            print("⚠️")
        }
        page++
    } while (result.isFailure || result.getOrThrow().isNotEmpty())
    println()
}

private fun AppConfig.importExtraFields() {
    // TODO: bulk insert
    runCatching {
        storesProvider.listSpecialFields().map { (storeId, extraFields) ->
            extraFields.forEach {
                storesRepository.saveExtraField(storeId, it.key, it.value)
            }
        }
    }.onFailure {
        println("failed to import extra fields: ${it.message}")
    }
}

private fun AppConfig.importSeasons() {
    // TODO: bulk insert
    runCatching {
        storesProvider.listSeasons()
            .map { storesRepository.saveSeasons(it.key, it.value) }
    }.onFailure {
        println("failed to import seasons: ${it.message}")
    }
}
