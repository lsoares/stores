package store.storerepository

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import store.domain.Store
import store.domain.StoreRepository

class StoreRepositoryPostgreSql(private val database: Database) : StoreRepository {

    init {
        transaction(database) {
            SchemaUtils.createMissingTablesAndColumns(StoreSchema)
        }
    }

    private object StoreSchema : Table("stores") {
        val id = varchar("id", 20).primaryKey()
        val name = varchar("name", 50).nullable()
        val description = varchar("description", 2000).nullable()
        val type = varchar("type", 40).nullable()
        val openingDate = varchar("opening_date", 10).nullable()
        val code = varchar("code", 70).nullable()
    }

    override fun list(page: Int) = transaction(database) {
        StoreSchema.selectAll()
            .orderBy(StoreSchema.id, SortOrder.DESC)
            .limit(10, page * 10)
            .map {
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

    override fun save(store: Store) {
        transaction(database) {
            StoreSchema.replace {
                it[id] = store.id
                it[name] = store.name
                it[code] = store.code
                it[description] = store.description
                it[type] = store.type
                it[openingDate] = store.openingDate
            }
        }
    }
}