package store.cloudtolocal

import store.AppConfig
import store.RealConfig
import store.storeprovider.StoreProviderClient.ListStoresResult.FailedToFetch
import store.storeprovider.StoreProviderClient.ListStoresResult.Valid
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun main() {
    Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate({
        RealConfig.importAllData()
    }, 0, 1, TimeUnit.HOURS)
}

private fun AppConfig.importAllData() {
    println("Importing stores info 🏬…")
    importStoresInfo()
    println("\nImporting extra fields…")
    importExtraFields()
    println("Importing seasons…")
    importSeasons()
    println("✅")
}

private tailrec fun AppConfig.importStoresInfo(page: Int = 1) {
    print(" $page")
    when (val result = storesProvider.listStores(page)) {
        is Valid -> {
            result.storeInfos.map(storesRepository::saveInfo)
            if (result.storeInfos.isNotEmpty()) {
                importStoresInfo(page + 1)
            }
        }
        is FailedToFetch -> {
            print("⚠️")
            importStoresInfo(page + 1)
        }
    }
}

private fun AppConfig.importExtraFields() {
    // TODO: bulk insert
    storesProvider.listSpecialFields().map { (storeId, extraFields) ->
        extraFields.forEach {
            storesRepository.saveExtraField(storeId, it.key, it.value)
        }
    }
}

private fun AppConfig.importSeasons() {
    // TODO: bulk insert
    storesProvider.listSeasons()
        .map { storesRepository.saveSeasons(it.key, it.value) }
}
