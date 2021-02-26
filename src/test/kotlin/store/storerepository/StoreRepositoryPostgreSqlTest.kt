package store.storerepository

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import store.domain.Store

class StoreRepositoryPostgreSqlTest {

    private val database = Database.connect(
        driver = "org.postgresql.Driver",
        url = "jdbc:postgresql://localhost:5430/stores_db_test",
        user = "kotlin_app_test",
        password = "abcde123_test"
    )

    @BeforeEach
    fun clear() {
        transaction(database) {
            SchemaUtils.drop(object : Table("stores") {})
        }
    }

    @Test
    fun `saves a new store`() {
        val storeRepository = StoreRepositoryPostgreSql(database)
        val storeToSave = Store(
            id = "101",
            name = "store 1",
            description = null,
            code = "code1",
            openingDate = "2021-02-07",
            type = "RETAIL",
        )

        storeRepository.save(storeToSave)

        assertEquals(storeToSave, storeRepository.list(0).single())
    }

    @Test
    fun `updates an existent store`() {
        val storeRepository = StoreRepositoryPostgreSql(database)
        val storeToSave = Store(
            id = "101",
            name = "store 1",
            description = null,
            code = "code1",
            openingDate = "2021-02-07",
            type = "RETAIL",
        )
        storeRepository.save(storeToSave)

        storeRepository.save(storeToSave.copy(description = "new description"))

        assertEquals(
            storeToSave.copy(description = "new description"),
            storeRepository.list(0).single()
        )
    }
}