package store.cloudtolocal

import store.RealConfig
import store.storeprovider.StoreProviderClient.ListStoresResult.FailedToFetch
import store.storeprovider.StoreProviderClient.ListStoresResult.Valid

fun main() {
    with(RealConfig) {
        importAllPages()
        println("✅")
        saveExtraFields()
        println("✅")
    }
}

private tailrec fun RealConfig.importAllPages(page: Int = 1) {
    println("Importing page $page")
    when (val result = storesProvider.listStores(page)) {
        is Valid -> {
            result.storeInfos.map(storesRepository::saveInfo)
            if (result.storeInfos.isNotEmpty()) importAllPages(page + 1)
        }
        is FailedToFetch -> {
            println("\tfailed")
            importAllPages(page + 1)
        }
    }
}

private fun RealConfig.saveExtraFields() {
    println("Saving extra fields")
    storesProvider.listSpecialFields().map { (storeId, extraFields) ->
        extraFields.forEach {
            storesRepository.saveExtraField(storeId, it.key, it.value)
        }
    }
}
