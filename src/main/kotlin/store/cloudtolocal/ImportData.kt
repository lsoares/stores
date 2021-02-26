package store.cloudtolocal

import store.RealConfig
import store.storeprovider.StoreProviderClient.ListStoresResult.FailedToFetch
import store.storeprovider.StoreProviderClient.ListStoresResult.Valid

fun main() {
    with(RealConfig) {
        importAllPages()
    }
}

private tailrec fun RealConfig.importAllPages(page: Int = 1) {
    println("Importing page $page")
    when (val result = storesProvider.listStores(page)) {
        is Valid -> {
            result.stores.map(storesRepository::save)
            if (result.stores.isNotEmpty()) importAllPages(page + 1)
        }
        is FailedToFetch -> {
            println("\tfailed")
            importAllPages(page + 1)
        }
    }
}