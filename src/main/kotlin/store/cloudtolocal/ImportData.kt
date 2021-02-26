package store.cloudtolocal

import store.RealConfig

fun main() {
    with(RealConfig) {
        storesProvider.listStores(page = 1).map {
            storesRepository.save(it)
        }
    }
}