package store.domain

interface StoreRepository {
    fun list(page: Int): List<Store>
    fun saveInfo(storeInfo: StoreInfo)
    fun saveExtraField(storeId: String, name: String, value: String)
}