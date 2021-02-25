package store.domain

interface StoreRepository {
    fun list(): List<Store>
}