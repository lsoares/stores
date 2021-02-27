package store.storerepository

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import store.domain.Store
import store.domain.Store.Season
import store.domain.StoreInfo
import store.domain.StoreRepository
import store.domain.StoreSeason

class StoreRepositoryPostgreSql(private val database: Database) : StoreRepository {

    init {
        transaction(database) {
            SchemaUtils.createMissingTablesAndColumns(
                StoreSchema,
                StoreExtraFieldSchema,
                StoreSeasonsSchema,
            )
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

    private object StoreExtraFieldSchema : Table("store_extra_fields") {
        val storeId = varchar("id", 20).references(StoreSchema.id, onDelete = ReferenceOption.CASCADE).primaryKey()
        val name = varchar("name", 80).primaryKey()
        val value = varchar("value", 150).nullable()
    }

    private object StoreSeasonsSchema : Table("store_seasons") {
        val storeId = varchar("id", 20).references(StoreSchema.id, onDelete = ReferenceOption.CASCADE).primaryKey()
        val year = integer("year").primaryKey()
        val season = varchar("season", 20).primaryKey()
    }

    override fun list(page: Int) = transaction(database) {
        StoreSchema.selectAll()
            .orderBy(StoreSchema.id, SortOrder.DESC)
            .limit(10, page * 10)
            .map {
                val storeId = it[StoreSchema.id]
                Store(
                    id = storeId,
                    name = it[StoreSchema.name],
                    code = it[StoreSchema.code],
                    description = it[StoreSchema.description],
                    type = it[StoreSchema.type],
                    openingDate = it[StoreSchema.openingDate],
                    extraFields = listExtraFields(storeId),
                    operationalDuring = listSeasons(storeId),
                )
            }
    }

    private fun listExtraFields(storeId: String) =
        StoreExtraFieldSchema.select { StoreExtraFieldSchema.storeId eq storeId }
            .map { it[StoreExtraFieldSchema.name] to it[StoreExtraFieldSchema.value] }
            .toMap()

    private fun listSeasons(storeId: String) =
        StoreSeasonsSchema.select { StoreSeasonsSchema.storeId eq storeId }
            .map { it[StoreSeasonsSchema.year] to Season.valueOf(it[StoreSeasonsSchema.season]) }
            .toSet()

    override fun saveInfo(storeInfo: StoreInfo) {
        transaction(database) {
            StoreSchema.replace {
                it[id] = storeInfo.id
                it[name] = storeInfo.name
                it[code] = storeInfo.code
                it[description] = storeInfo.description
                it[type] = storeInfo.type
                it[openingDate] = storeInfo.openingDate
            }
        }
    }

    override fun saveExtraField(storeId: String, name: String, value: String) {
        transaction(database) {
            if (StoreSchema.select { StoreSchema.id eq storeId }.count() == 0) return@transaction

            StoreExtraFieldSchema.replace {
                it[StoreExtraFieldSchema.storeId] = storeId
                it[StoreExtraFieldSchema.name] = name
                it[StoreExtraFieldSchema.value] = value
            }
        }
    }

    override fun saveSeason(storeSeason: StoreSeason) {
        transaction(database) {
            if (StoreSchema.select { StoreSchema.id eq storeSeason.storeId }.count() == 0) return@transaction

            StoreSeasonsSchema.replace {
                it[storeId] = storeSeason.storeId
                it[season] = storeSeason.season.name
                it[year] = storeSeason.year
            }
        }
    }
}