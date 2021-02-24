package domain

interface StoreRepository {
    fun list(): List<Store>
}