package store.domain

interface StoreRepository {
    fun list(page: Int): List<Store>
}