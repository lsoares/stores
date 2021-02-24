package domain

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ListStoresTestHandlerUseCaseTest {

    @Test
    fun `it returns a list of stores from the repo`() {
        val repository = mockk<StoreRepository> {
            every { list() } returns listOf(
                Store(
                    id = 123,
                    name = "store 1",
                )
            )
        }
        val listStores = ListStoresUseCase(repository)

        val stores = listStores()

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