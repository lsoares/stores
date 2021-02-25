package store.restapi

import store.domain.ListStoresUseCase
import store.domain.Store
import store.domain.StoreRepository

abstract class Dependencies {
    abstract val storesRepository: StoreRepository
    open val listStores by lazy { ListStoresUseCase(storesRepository) }
}

object MockConfig : Dependencies() {
    override val storesRepository by lazy {
        object : StoreRepository {
            override fun list(): List<Store> =
                listOf(Store(1, "abc"), Store(2, "xyz"))
        }
    }
}