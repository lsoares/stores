package store.domain

class ListStoresUseCase(private val repository: StoreRepository) {

    operator fun invoke(page: Int, nameSearch: String?) =
        repository.list(page, nameSearch)
}