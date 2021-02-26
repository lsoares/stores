package store.domain

interface StoreRepository {
    fun list(page: Int): List<Store>
    fun save(store: Store)
}