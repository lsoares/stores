package store.domain

interface StoreRepository {
    fun list(page: Int, nameSearch: String? = null): List<Store>
    fun saveInfo(storeInfo: StoreInfo)
    fun saveExtraField(storeId: String, name: String, value: String)
    fun saveSeasons(storeId: String, seasons: Set<String>)
}