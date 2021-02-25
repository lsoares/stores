package store.domain

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ListStoresUseCaseTest {

    @Test
    fun `returns a list of stores from the repo`() {
        val repository = mockk<StoreRepository> {
            every { list(2) } returns listOf(
                Store(
                    id = 123,
                    name = "store 1",
                )
            )
        }
        val listStores = ListStoresUseCase(repository)

        val stores = listStores(2)

        assertEquals(
            listOf(
                Store(
                    id = 123,
                    name = "store 1",
                )
            ),
            stores
        )
    }
}