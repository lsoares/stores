package store.storerepository

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import store.domain.Store
import store.domain.StoreInfo

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

        storeRepository.saveSeasons("101", setOf("00 H2", "22 H2"))

        assertEquals(
            Store(
                id = "101",
                name = "store 1",
                description = null,
                code = "code1",
                openingDate = "2021-02-07",
                type = "RETAIL",
                seasons = setOf("00 H2", "22 H2"),
            ),
            storeRepository.list(0).single()
        )
    }

    @Test
    fun `ignores a season for non-existent store`() {
        val storeRepository = StoreRepositoryPostgreSql(database)
        storeRepository.saveInfo(storeInfo)

        storeRepository.saveSeasons("999", setOf("2020 H2"))
    }
}