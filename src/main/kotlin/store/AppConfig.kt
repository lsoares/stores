package store

import org.jetbrains.exposed.sql.Database
import store.domain.ListStoresUseCase
import store.domain.SetStoreNameUseCase
import store.storeprovider.StoreProviderClient
import store.storerepository.StoreRepositoryPostgreSql

abstract class AppConfig {
    abstract val storesRepository: store.domain.StoreRepository
    val storesProvider by lazy {
        StoreProviderClient(
            baseUrl = System.getenv("STORES_API_BASE_URL"),
            apiKey = System.getenv("STORES_API_KEY"),
        )
    }
    open val listStores by lazy { ListStoresUseCase(storesRepository) }
    open val setStoreName by lazy { SetStoreNameUseCase(storesRepository) }
}

object RealConfig : AppConfig() {
    override val storesRepository by lazy {
        StoreRepositoryPostgreSql(
            Database.connect(
                driver = "org.postgresql.Driver",
                url = System.getenv("DATABASE_URL"),
                user = System.getenv("DATABASE_USER"),
                password = System.getenv("DATABASE_PASSWORD"),
            )
        )
    }
}
