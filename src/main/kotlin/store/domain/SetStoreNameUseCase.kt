package store.domain

class SetStoreNameUseCase(private val storesRepository: StoreRepository) {

    operator fun invoke(storeId: String, newStoreName: String) {
        require(newStoreName.length >= 3)
        storesRepository.setCustomStoreName(storeId, newStoreName)
    }
}