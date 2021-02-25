package store.restapi

import store.domain.ListStoresUseCase
import store.domain.Store
import store.domain.StoreRepository

abstract class AppConfig {
    abstract val storesRepository: StoreRepository
    open val listStores by lazy { ListStoresUseCase(storesRepository) }
}

object StubbedConfig : AppConfig() {
    override val storesRepository by lazy {
        object : StoreRepository {
            override fun list(page: Int) =
                when (page) {
                    in 1..5 -> (1..10).map {
                        Store(it * page, "abc ${it * page}")
                    }
                    else -> emptyList()
                }
        }
    }
}