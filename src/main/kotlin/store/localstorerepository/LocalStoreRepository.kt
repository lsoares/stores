package store.localstorerepository

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import store.domain.Store
import store.domain.StoreRepository

class LocalStoreRepository(private val database: Database) : StoreRepository {

    init {
        transaction {
            SchemaUtils.create(StoreSchema)
        }
    }

    private object StoreSchema : Table("stores") {
        val id = varchar("id", 20).primaryKey()
        val name = varchar("name", 50)
        val description = varchar("description", 2000)
        val type = varchar("type", 40)
        val openingDate = varchar("opening_date", 10) // TODO: let's be lean and use String
        val code = varchar("code", 40)
    }

    override fun list(page: Int) = transaction {
        StoreSchema.selectAll().map {
            Store(
                id = it[StoreSchema.id],
                name = it[StoreSchema.name],
                code = it[StoreSchema.code],
                description = it[StoreSchema.description],
                type = it[StoreSchema.type],
                openingDate = it[StoreSchema.openingDate],
            )
        }
    }
}