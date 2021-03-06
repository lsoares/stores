package store.domain

interface StoreRepository {
    fun list(page: Int? = null, nameSearch: String? = null): List<Store>
    fun saveInfo(storeInfo: StoreInfo)
    fun saveExtraField(storeId: String, name: String, value: String)
    fun saveSeasons(storeId: String, seasons: Set<String>)
    fun setCustomStoreName(storeId: String, newName: String)
}