package store.domain

class ListStoresUseCase(private val repository: StoreRepository) {

    operator fun invoke() = repository.list()
}