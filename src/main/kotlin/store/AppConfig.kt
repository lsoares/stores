package store.restapi

import store.domain.ListStoresUseCase
import store.domain.Store
import store.domain.StoreRepository
import store.storeprovider.StoreProviderClient

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
                        Store(
                            id = it * page,
                            name = "name ${it * page}",
                            code = "code ${it * page}",
                            description =  "description ${it * page}",
                            openingDate = "date",
                            type = "type ${it * page}",
                        )
                    }
                    else -> emptyList()
                }
        }
    }
}

object RealConfig : AppConfig() {
    override val storesRepository by lazy {
        StoreProviderClient(
            // TODO: in real-life, this should never be here; use env vars instead
            baseUrl = "http://134.209.29.209",
            apiKey = "76a325g7g2ahs7h4673aa25s47632h5362a4532642",
        )
    }
}