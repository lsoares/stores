package store.domain

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail

class SetStoreNameIUseCaseTest {

    @Test
    fun `does not allow store names with less than 3 chars`() {
        val setStoreNameUseCase = SetStoreNameUseCase(object : StoreRepository {
            override fun list(page: Int?, nameSearch: String?) = fail("no need to list")
            override fun saveInfo(storeInfo: StoreInfo) = fail("no need to save")
            override fun saveExtraField(storeId: String, name: String, value: String) = fail("no need to save")
            override fun saveSeasons(storeId: String, seasons: Set<String>) = fail("no need to save")
            override fun setCustomStoreName(storeId: String, newName: String): Unit = fail("no need to save")
        })

        val set = { setStoreNameUseCase("id123", "aa") }

        assertThrows<Exception> { set() }
    }
}