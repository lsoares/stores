package store.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail

class SetStoreNameIUseCaseTest {

    @Test
    fun `calls the repository if name is valid`() {
        var usedStoreId: String? = null
        var usedName: String? = null
        val setStoreNameUseCase = SetStoreNameUseCase(object : TestDoubleRepo() {
            override fun setCustomStoreName(storeId: String, newName: String) {
                usedStoreId = storeId
                usedName = newName
            }
        })

        setStoreNameUseCase("id123", " store 1   ")

        assertEquals("id123", usedStoreId)
        assertEquals("store 1", usedName)
    }

    @Test
    fun `does not allow store names with invalid size`() {
        val setStoreNameUseCase = SetStoreNameUseCase(object : TestDoubleRepo() {})

        val set1 = { setStoreNameUseCase("id123", "   aa   ") }
        val set2 = { setStoreNameUseCase("id123", "x".repeat(51)) }

        assertThrows<Exception> { set1() }
        assertThrows<Exception> { set2() }
    }

    private open class TestDoubleRepo : StoreRepository {
        override fun list(page: Int?, nameSearch: String?): List<Store> = fail("no need to list")
        override fun saveInfo(storeInfo: StoreInfo): Unit = fail("no need to save")
        override fun saveExtraField(storeId: String, name: String, value: String): Unit = fail("no need to save")
        override fun saveSeasons(storeId: String, seasons: Set<String>): Unit = fail("no need to save")
        override fun setCustomStoreName(storeId: String, newName: String): Unit = fail("no need to save")
    }
}