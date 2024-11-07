import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object ClientsTable : Table("clients") {
    val id = integer("id_client").autoIncrement()
    val name = varchar("name_client", 50)
    val contacts = varchar("contacts_client", 50)

    override val primaryKey = PrimaryKey(id)
}

object OrdersTable : Table("orders") {
    val id = integer("id_order").autoIncrement()
    val dateOfFormation = datetime("date_of_formation")
    val deadline = datetime("deadline")
    val cost = integer("cost")
    val clientId = integer("id_client").references(ClientsTable.id)
    val deliveryAddress = varchar("del_address", 50).nullable()

    override val primaryKey = PrimaryKey(id)
}

object ContentOfOrdersTable : Table("content_of_orders") {
    val id = integer("id_contents").autoIncrement()
    val orderId = integer("id_order").references(OrdersTable.id)
    val modelId = integer("id_model").references(ModelsTable.id)
    val count = integer("count")

    override val primaryKey = PrimaryKey(id)
}

object EquipmentsTable : Table("equipments") {
    val id = integer("id_equipment").autoIncrement()
    val name = varchar("name_equipment", 50)
    val description = varchar("desc_equipment", 50)

    override val primaryKey = PrimaryKey(id)
}

object MaterialsTable : Table("materials") {
    val id = integer("id_material").autoIncrement()
    val name = varchar("name_material", 20)
    val description = varchar("desc_material", 50)

    override val primaryKey = PrimaryKey(id)
}

object MaterialsInProductionTable : Table("materials_in_production") {
    val materialId = integer("id_material").references(MaterialsTable.id)
    val productionId = integer("id_production").references(ProductionTable.id)
    val count = integer("count")

    override val primaryKey = PrimaryKey(materialId, productionId)
}

object ModelsTable : Table("models") {
    val id = integer("id_model").autoIncrement()
    val width = integer("width_model")
    val length = integer("lenght_model")
    val height = integer("height_model")
    val description = varchar("desc_model", 50)

    override val primaryKey = PrimaryKey(id)
}

object ProductionTable : Table("production") {
    val id = integer("id_production").autoIncrement()
    val contentId = integer("id_contents").references(ContentOfOrdersTable.id)
    val dateOfWork = datetime("date_of_work")
    val workerId = integer("id_worker").references(WorkersTable.id)
    val equipmentId = integer("id_equipment").references(EquipmentsTable.id)
    val typeOfWorkId = integer("id_type_of_work").references(TypeOfWorkTable.id)

    override val primaryKey = PrimaryKey(id)
}

object StatesTable : Table("states") {
    val equipmentId = integer("id_equipment").references(EquipmentsTable.id)
    val state = varchar("state", 20)

    override val primaryKey = PrimaryKey(equipmentId)
}

object TypeOfWorkTable : Table("type_of_work") {
    val id = integer("id_type").autoIncrement()
    val name = varchar("name_type", 25)

    override val primaryKey = PrimaryKey(id)
}

object WorkersTable : Table("workers") {
    val id = integer("id_worker").autoIncrement()
    val name = varchar("name_worker", 50)
    val position = varchar("position_worker", 30)
    val contacts = varchar("contacts_worker", 50)

    override val primaryKey = PrimaryKey(id)
}


