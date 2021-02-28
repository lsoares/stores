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
            "new description",
            storeRepository.list(0).single().description
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
            mapOf("a" to "1", "b" to "3"),
            storeRepository.list(0).single().extraFields
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
            setOf("00 H2", "22 H2"),
            storeRepository.list(0).single().seasons
        )
    }

    @Test
    fun `ignores a season for non-existent store`() {
        val storeRepository = StoreRepositoryPostgreSql(database)
        storeRepository.saveInfo(storeInfo)

        storeRepository.saveSeasons("999", setOf("2020 H2"))
    }

    @Test
    fun `searches by text`() {
        val storeRepository = StoreRepositoryPostgreSql(database)

        storeRepository.saveInfo(storeInfo.copy(name = "A Typical Store Name"))
        storeRepository.saveInfo(storeInfo.copy(id = "321", name = "other store"))

        assertEquals(
            Store(
                id = "101",
                name = "A Typical Store Name",
                description = null,
                code = "code1",
                openingDate = "2021-02-07",
                type = "RETAIL",
            ),
            storeRepository.list(0, "Typical").single()
        )
    }

    @Test
    fun `updates store name`() {
        val storeRepository = StoreRepositoryPostgreSql(database)
        storeRepository.saveInfo(storeInfo.copy(name = "old name"))

        storeRepository.updateStoreName("101", "new name")

        assertEquals(
            "new name",
            storeRepository.list(0).single().name
        )
    }

    @Test
    fun `ignores a non-existent store`() {
        val storeRepository = StoreRepositoryPostgreSql(database)

        storeRepository.updateStoreName("999", "new name")
    }

    @Test
    fun `does not loose user provided name when updating it`() {
        val storeRepository = StoreRepositoryPostgreSql(database)
        storeRepository.saveInfo(storeInfo)
        storeRepository.updateStoreName(storeInfo.id, "new name")
        storeRepository.saveInfo(storeInfo.copy(name = "try overwriting name"))

        assertEquals(
            "new name",
            storeRepository.list(0).single().name
        )
    }
}