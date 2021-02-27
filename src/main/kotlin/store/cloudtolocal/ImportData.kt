package store.cloudtolocal

import store.RealConfig
import store.storeprovider.StoreProviderClient.ListStoresResult.FailedToFetch
import store.storeprovider.StoreProviderClient.ListStoresResult.Valid

fun main() {
    with(RealConfig) {
        println("Importing stores info 🏬…")
        importStoresInfo()
        println("\nImporting extra fields…")
        importExtraFields()
        println("Importing seasons…")
        importSeasons()
        println("✅")
    }
}

private tailrec fun RealConfig.importStoresInfo(page: Int = 1) {
    print(" $page")
    when (val result = storesProvider.listStores(page)) {
        is Valid -> {
            result.storeInfos.map(storesRepository::saveInfo)
            if (result.storeInfos.isNotEmpty()) importStoresInfo(page + 1)
        }
        is FailedToFetch -> {
            print("⚠️")
            importStoresInfo(page + 1)
        }
    }
}

private fun RealConfig.importExtraFields() {
    storesProvider.listSpecialFields().map { (storeId, extraFields) ->
        extraFields.forEach {
            storesRepository.saveExtraField(storeId, it.key, it.value)
        }
    }
}

private fun RealConfig.importSeasons() {
    storesProvider.listSeasons()
        .map { storesRepository.saveSeasons(it.key, it.value) }
}
