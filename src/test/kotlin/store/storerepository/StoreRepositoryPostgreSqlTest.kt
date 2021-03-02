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
import java.text.SimpleDateFormat

class StoreRepositoryPostgreSqlTest {

    private val database = Database.connect(
        driver = "org.postgresql.Driver",
        url = "jdbc:postgresql://localhost:5430/stores_db_test",
        user = "kotlin_app_test",
        password = "abcde123_test"
    )

    private val storeRepository = StoreRepositoryPostgreSql(database)

    private val storeInfo = StoreInfo(
        externalId = "101",
        name = "store 1",
        description = null,
        code = "code1",
        openingDate = SimpleDateFormat("yyyy-MM-dd").parse("2021-02-07"),
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
        storeRepository.saveInfo(storeInfo)

        assertEquals(
            Store(
                id = 1,
                externalId = "101",
                name = "store 1",
                description = null,
                code = "code1",
                openingDate = SimpleDateFormat("yyyy-MM-dd")
                    .parse("2021-02-07"),
                type = "RETAIL",
            ),
            storeRepository.list(0).single().copy(id = 1)
        )
    }

    @Test
    fun `updates an existent store info`() {
        storeRepository.saveInfo(storeInfo)

        storeRepository.saveInfo(storeInfo.copy(description = "new description"))

        assertEquals(
            "new description",
            storeRepository.list(0).single().description
        )
    }

    @Test
    fun `updates an existent store info but leaves name intact if it was changed by user`() {
        storeRepository.saveInfo(storeInfo)
        storeRepository.setCustomStoreName(storeInfo.externalId, "custom name")

        storeRepository.saveInfo(storeInfo.copy(name = "try override"))

        assertEquals(
            "custom name",
            storeRepository.list(0).single().name
        )
    }

    @Test
    fun `saves store extra fields`() {
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
        storeRepository.saveExtraField("101", "b", "3")
    }

    @Test
    fun `saves a store season`() {
        storeRepository.saveInfo(storeInfo)

        storeRepository.saveSeasons("101", setOf("00 H2", "22 H2"))

        assertEquals(
            setOf("00 H2", "22 H2"),
            storeRepository.list(0).single().seasons
        )
    }

    @Test
    fun `ignores a season for non-existent store`() {
        storeRepository.saveInfo(storeInfo)

        storeRepository.saveSeasons("999", setOf("2020 H2"))
    }

    @Test
    fun `searches by text`() {
        storeRepository.saveInfo(storeInfo.copy(name = "A Typical Store Name"))
        storeRepository.saveInfo(storeInfo.copy(externalId = "321", name = "other store"))

        val list = storeRepository.list(0, "typical")

        assertEquals(
            "A Typical Store Name",
            list.single().name
        )
    }

    @Test
    fun `updates store name`() {
        storeRepository.saveInfo(storeInfo.copy(name = "old name"))

        storeRepository.setCustomStoreName("101", "new name")

        assertEquals(
            "new name",
            storeRepository.list(0).single().name
        )
    }

    @Test
    fun `ignores a non-existent store`() {
        storeRepository.setCustomStoreName("999", "new name")
    }
}