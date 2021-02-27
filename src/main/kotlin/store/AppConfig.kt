package store

import org.jetbrains.exposed.sql.Database
import store.domain.ListStoresUseCase
import store.storeprovider.StoreProviderClient
import store.storerepository.StoreRepositoryPostgreSql

abstract class AppConfig {
    abstract val storesRepository: store.domain.StoreRepository
    val storesProvider by lazy {
        StoreProviderClient(
            baseUrl = System.getenv("API_BASE_URL"),
            apiKey = System.getenv("API_KEY"),
        )
    }
    open val listStores by lazy { ListStoresUseCase(storesRepository) }
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
