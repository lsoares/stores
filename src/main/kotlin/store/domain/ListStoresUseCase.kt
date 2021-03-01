package store.domain

class ListStoresUseCase(private val repository: StoreRepository) {

    operator fun invoke(page: Int? = null, nameSearch: String? = null) =
        repository.list(page, nameSearch)
}
