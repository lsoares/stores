package store.storerepository

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import store.domain.Store
import store.domain.StoreInfo
import store.domain.StoreRepository
import java.sql.Blob
import javax.sql.rowset.serial.SerialBlob

class StoreRepositoryPostgreSql(private val database: Database) : StoreRepository {

    init {
        transaction(database) {
            SchemaUtils.createMissingTablesAndColumns(
                StoreSchema,
                StoreExtraFieldSchema,
            )
        }
    }

    private object StoreSchema : Table("stores") {
        val id = varchar("id", 20).primaryKey()
        val name = varchar("name", 50).nullable()
        val customName = varchar("custom_name", 50).nullable()
        val description = varchar("description", 2000).nullable()
        val type = varchar("type", 40).nullable()
        val openingDate = date("opening_date").nullable()
        val code = varchar("code", 70).nullable()
        val seasons = blob("seasons_json").nullable()
    }

    private object StoreExtraFieldSchema : Table("store_extra_fields") {
        val storeId = varchar("id", 20).references(StoreSchema.id, onDelete = ReferenceOption.CASCADE).primaryKey()
        val name = varchar("name", 80).primaryKey()
        val value = varchar("value", 150).nullable()
    }

    override fun list(page: Int?, nameSearch: String?) = transaction(database) {
        StoreSchema.selectAll()
            .orderBy(StoreSchema.openingDate, SortOrder.DESC)
            .apply {
                page?.let {
                    limit(10, page * 10)
                }
                nameSearch?.takeUnless(String::isBlank)?.let {
                    adjustWhere {
                        Op.build { StoreSchema.name.lowerCase() like "%$it%".toLowerCase() }
                    }
                }
            }.map {
                Store(
                    id = it[StoreSchema.id],
                    name = it[StoreSchema.customName] ?: it[StoreSchema.name],
                    code = it[StoreSchema.code],
                    description = it[StoreSchema.description],
                    type = it[StoreSchema.type],
                    openingDate = it[StoreSchema.openingDate]?.toDate(),
                    extraFields = listExtraFields(it[StoreSchema.id]),
                    seasons = it[StoreSchema.seasons]?.parseSeasons() ?: emptySet()
                )
            }
    }

    private fun Blob.parseSeasons() =
        objectMapper.readValue(binaryStream, object : TypeReference<List<String>>() {}).toSet()

    private fun listExtraFields(storeId: String) =
        StoreExtraFieldSchema.select { StoreExtraFieldSchema.storeId eq storeId }
            .map { it[StoreExtraFieldSchema.name] to it[StoreExtraFieldSchema.value] }
            .toMap()

    override fun saveInfo(storeInfo: StoreInfo) { // TODO should receive store
        transaction(database) {
            if (StoreSchema.select { StoreSchema.id eq storeInfo.id }.count() == 0) {
                StoreSchema.insert {
                    it[id] = storeInfo.id
                    it[name] = storeInfo.name
                    it[code] = storeInfo.code
                    it[description] = storeInfo.description
                    it[type] = storeInfo.type
                    it[openingDate] = storeInfo.openingDate?.let { DateTime(it) }
                }
            } else {
                StoreSchema.update({ StoreSchema.id eq storeInfo.id }) {
                    it[id] = storeInfo.id
                    it[name] = storeInfo.name
                    it[code] = storeInfo.code
                    it[description] = storeInfo.description
                    it[type] = storeInfo.type
                    it[openingDate] = storeInfo.openingDate?.let { DateTime(it) }
                }
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

    override fun saveSeasons(storeId: String, seasons: Set<String>) {
        transaction(database) {
            StoreSchema.update({ StoreSchema.id eq storeId }) {
                it[StoreSchema.seasons] = SerialBlob(objectMapper.writeValueAsString(seasons).toByteArray())
            }
        }
    }

    override fun setCustomStoreName(storeId: String, newName: String) {
        transaction(database) {
            StoreSchema.update({ StoreSchema.id eq storeId }) {
                it[customName] = newName
            }
        }
    }
}

private val objectMapper = ObjectMapper()
