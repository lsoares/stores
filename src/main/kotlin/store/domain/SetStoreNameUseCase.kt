package store.domain

class SetStoreNameUseCase(private val storesRepository: StoreRepository) {

    operator fun invoke(storeId: String, newStoreName: String) {
        with(newStoreName.trim()) {
            require(length in 3..50)

            storesRepository.setCustomStoreName(storeId, this)
        }
    }
}