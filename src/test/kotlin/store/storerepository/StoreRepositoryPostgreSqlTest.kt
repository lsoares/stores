package store.storerepository

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import store.domain.Store
import store.domain.Store.Season.FIRST_HALF
import store.domain.Store.Season.SECOND_HALF
import store.domain.StoreInfo
import store.domain.StoreSeason

class StoreRepositoryPostgreSqlTest {

    private val database = Database.connect(
        driver = "org.postgresql.Driver",
        url = "jdbc:postgresql://localhost:5430/stores_db_test",
        user = "kotlin_app_test",
        password = "abcde123_test"
    )

    private val storeInfo = StoreInfo(
        id = "101",
        name = "store 1",
        description = null,
        code = "code1",
        openingDate = "2021-02-07",
        type = "RETAIL",
    )

    @BeforeEach
    fun clear() {
        transaction(database) {
            object : Table("stores") {}.deleteAll()
        }
    }

    @Test
    fun `saves a new store info`() {
        val storeRepository = StoreRepositoryPostgreSql(database)

        storeRepository.saveInfo(storeInfo)

        assertEquals(
            Store(
                id = "101",
                name = "store 1",
                description = null,
                code = "code1",
                openingDate = "2021-02-07",
                type = "RETAIL",
            ),
            storeRepository.list(0).single()
        )
    }

    @Test
    fun `updates an existent store info`() {
        val storeRepository = StoreRepositoryPostgreSql(database)
        storeRepository.saveInfo(storeInfo)

        storeRepository.saveInfo(storeInfo.copy(description = "new description"))

        assertEquals(
            Store(
                id = "101",
                name = "store 1",
                description = "new description",
                code = "code1",
                openingDate = "2021-02-07",
                type = "RETAIL",
            ),
            storeRepository.list(0).single()
        )
    }

    @Test
    fun `saves store extra fields`() {
        val storeRepository = StoreRepositoryPostgreSql(database)
        storeRepository.saveInfo(storeInfo)

        storeRepository.saveExtraField("101", "a", "1")
        storeRepository.saveExtraField("101", "b", "2")
        storeRepository.saveExtraField("101", "b", "3")

        assertEquals(
            Store(
                id = "101",
                name = "store 1",
                description = null,
                code = "code1",
                openingDate = "2021-02-07",
                type = "RETAIL",
                extraFields = mapOf("a" to "1", "b" to "3")
            ),
            storeRepository.list(0).single()
        )
    }

    @Test
    fun `ignores extra fields that belongs to non-existent store`() {
        val storeRepository = StoreRepositoryPostgreSql(database)

        storeRepository.saveExtraField("101", "b", "3")
    }

    @Test
    fun `saves a store season`() {
        val storeRepository = StoreRepositoryPostgreSql(database)
        storeRepository.saveInfo(storeInfo)

        storeRepository.saveSeason(StoreSeason(
            storeId = "101",
            year = 2000,
            season = SECOND_HALF,
        ))
        storeRepository.saveSeason(StoreSeason(
            storeId = "101",
            year = 2022,
            season = FIRST_HALF,
        ))

        assertEquals(
            Store(
                id = "101",
                name = "store 1",
                description = null,
                code = "code1",
                openingDate = "2021-02-07",
                type = "RETAIL",
                operationalDuring = setOf(
                    2000 to SECOND_HALF,
                    2022 to FIRST_HALF,
                )
            ),
            storeRepository.list(0).single()
        )
    }

    @Test
    fun `ignores a season for non-existent store`() {
        val storeRepository = StoreRepositoryPostgreSql(database)
        storeRepository.saveInfo(storeInfo)

        storeRepository.saveSeason(StoreSeason(
            storeId = "999",
            year = 2022,
            season = FIRST_HALF,
        ))
    }
}