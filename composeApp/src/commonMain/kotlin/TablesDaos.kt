import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

class ClientDao {
    suspend fun getAllClients(): List<Client> =
        transaction {
            ClientsTable.selectAll()
                .map { it.toClient() }
        }

    suspend fun getClient(id: Int): Client? =
        transaction {
            ClientsTable
                .select { ClientsTable.id eq id }
                .map { it.toClient() }
                .singleOrNull()
        }

    suspend fun addClient(name: String, contacts: String): Client? =
        transaction {
            val insertStatement = ClientsTable.insert {
                it[ClientsTable.name] = name
                it[ClientsTable.contacts] = contacts
            }
            insertStatement.resultedValues?.first()?.toClient()
        }

    companion object {
        private fun ResultRow.toClient() = Client(
            idClient = this[ClientsTable.id],
            nameClient = this[ClientsTable.name],
            contactsClient = this[ClientsTable.contacts]
        )
    }
}

class ContentOfOrderDao {
    suspend fun getAllContentOfOrders(): List<ContentOfOrder> =
        transaction {
            ContentOfOrdersTable.selectAll()
                .map { it.toContentOfOrder() }
        }

    suspend fun getContentOfOrder(id: Int): ContentOfOrder? =
        transaction {
            ContentOfOrdersTable
                .select { ContentOfOrdersTable.id eq id }
                .map { it.toContentOfOrder() }
                .singleOrNull()
        }

    suspend fun addContentOfOrder(idOrder: Int, idModel: Int, count: Int): ContentOfOrder? =
        transaction {
            val insertStatement = ContentOfOrdersTable.insert {
                it[orderId] = idOrder
                it[modelId] = idModel
                it[ContentOfOrdersTable.count] = count
            }
            insertStatement.resultedValues?.first()?.toContentOfOrder()
        }

    companion object {
        private fun ResultRow.toContentOfOrder() = ContentOfOrder(
            idContents = this[ContentOfOrdersTable.id],
            idOrder = this[ContentOfOrdersTable.orderId],
            idModel = this[ContentOfOrdersTable.modelId],
            count = this[ContentOfOrdersTable.count]
        )
    }
}

class EquipmentDao {
    suspend fun getAllEquipments(): List<Equipment> =
        transaction {
            EquipmentsTable.selectAll()
                .map { it.toEquipment() }
        }

    suspend fun getEquipment(id: Int): Equipment? =
        transaction {
            EquipmentsTable
                .select { EquipmentsTable.id eq id }
                .map { it.toEquipment() }
                .singleOrNull()
        }

    suspend fun addEquipment(name: String, desc: String): Equipment? =
        transaction {
            val insertStatement = EquipmentsTable.insert {
                it[EquipmentsTable.name] = name
                it[description] = desc
            }
            insertStatement.resultedValues?.first()?.toEquipment()
        }

    companion object {
        private fun ResultRow.toEquipment() = Equipment(
            idEquipment = this[EquipmentsTable.id],
            nameEquipment = this[EquipmentsTable.name],
            descEquipment = this[EquipmentsTable.description]
        )
    }
}

class MaterialDao {
    suspend fun getAllMaterials(): List<Material> =
        transaction {
            MaterialsTable.selectAll()
                .map { it.toMaterial() }
        }

    suspend fun getMaterial(id: Int): Material? =
        transaction {
            MaterialsTable
                .select { MaterialsTable.id eq id }
                .map { it.toMaterial() }
                .singleOrNull()
        }

    suspend fun addMaterial(name: String, desc: String): Material? =
        transaction {
            val insertStatement = MaterialsTable.insert {
                it[MaterialsTable.name] = name
                it[description] = desc
            }
            insertStatement.resultedValues?.first()?.toMaterial()
        }

    companion object {
        private fun ResultRow.toMaterial() = Material(
            idMaterial = this[MaterialsTable.id],
            nameMaterial = this[MaterialsTable.name],
            descMaterial = this[MaterialsTable.description]
        )
    }
}

class MaterialInProductionDao {
    fun getAllContentOfOrders(): List<MaterialInProduction> =
        transaction {
            MaterialsInProductionTable.selectAll()
                .map { it.toMaterialInProduction() }
        }

    suspend fun getMaterialInProduction(productionId: Int, materialID: Int): MaterialInProduction? =
        transaction {
            MaterialsInProductionTable
                .select {
                    MaterialsInProductionTable.materialId eq materialID
                    MaterialsInProductionTable.productionId eq productionId
                }
                .map { it.toMaterialInProduction() }
                .singleOrNull()
        }

    suspend fun addMaterialsInProduction(idProduction: Int, idMaterial: Int, count: Int): MaterialInProduction? =
        transaction {
            val insertStatement = MaterialsInProductionTable.insert {
                it[productionId] = idProduction
                it[materialId] = idMaterial
                it[MaterialsInProductionTable.count] = count
            }
            insertStatement.resultedValues?.first()?.toMaterialInProduction()
        }

    companion object {
        private fun ResultRow.toMaterialInProduction() = MaterialInProduction(
            idProduction = this[MaterialsInProductionTable.productionId],
            idMaterial = this[MaterialsInProductionTable.materialId],
            count = this[MaterialsInProductionTable.count]
        )
    }
}

class ModelsDao {
    suspend fun getAllModels(): List<Model> =
        transaction {
            ModelsTable.selectAll()
                .map { it.toModel() }
        }

    suspend fun getModel(id: Int): Model? =
        transaction {
            ModelsTable
                .select { ModelsTable.id eq id }
                .map { it.toModel() }
                .singleOrNull()
        }

    suspend fun deleteModel(id: Int) =
        transaction {
            ModelsTable.deleteWhere { ModelsTable.id eq id }
            exec("ALTER TABLE watch_production.models AUTO_INCREMENT = ${id-1}")
        }

    suspend fun updateModel(id: Int, width: Int, length: Int, height: Int, desc: String) =
        transaction {
            ModelsTable.update({ModelsTable.id eq id}) {
                it[ModelsTable.width] = width
                it[ModelsTable.length] = length
                it[ModelsTable.height] = height
                it[description] = desc
            }
        }

    suspend fun alterModel(newId: Int) =
        transaction {

        }


    suspend fun addModel(width: Int, length: Int, height: Int, desc: String): Model? =
        transaction {
            val insertStatement = ModelsTable.insert {
                it[ModelsTable.width] = width
                it[ModelsTable.length] = length
                it[ModelsTable.height] = height
                it[description] = desc
            }
            insertStatement.resultedValues?.first()?.toModel()
        }

    companion object {
        private fun ResultRow.toModel() = Model(
            idModel = this[ModelsTable.id],
            widthModel = this[ModelsTable.width],
            lengthModel = this[ModelsTable.length],
            heightModel = this[ModelsTable.height],
            descModel = this[ModelsTable.description]
        )
    }
}

class OrderDao {
    suspend fun getAllOrders(): List<Order> =
        transaction {
            OrdersTable.selectAll()
                .map { it.toOrder() }
        }

    suspend fun getOrder(id: Int): Order? =
        transaction {
            OrdersTable
                .select { OrdersTable.id eq id }
                .map { it.toOrder() }
                .singleOrNull()
        }

    suspend fun addOrder(idClient: Int, formationDate: LocalDateTime, deadline: LocalDateTime, cost: Int, address: String): Order? =
        transaction {
            val insertStatement = OrdersTable.insert {
                it[id] = idClient
                it[dateOfFormation] = formationDate
                it[OrdersTable.deadline] = deadline
                it[OrdersTable.cost] = cost
                it[deliveryAddress] = address
            }
            insertStatement.resultedValues?.first()?.toOrder()
        }

    companion object {
        private fun ResultRow.toOrder() = Order(
            idOrder = this[OrdersTable.id],
            idClient = this[OrdersTable.clientId],
            dateOfFormation = this[OrdersTable.dateOfFormation],
            deadline = this[OrdersTable.deadline],
            cost = this[OrdersTable.cost],
            delAddress = this[OrdersTable.deliveryAddress]
        )
    }
}

class StateDao {
    suspend fun getAllStates(): List<State> =
        transaction {
            StatesTable.selectAll()
                .map { it.toState() }
        }

    suspend fun getState(id: Int): State? =
        transaction {
            StatesTable
                .select { StatesTable.equipmentId eq id }
                .map { it.toState() }
                .singleOrNull()
        }

    suspend fun addState(equipmentId:Int, state: String): State? =
        transaction {
            val insertStatement = StatesTable.insert {
                it[StatesTable.equipmentId] = equipmentId
                it[StatesTable.state] = state
            }
            insertStatement.resultedValues?.first()?.toState()
        }

    companion object {
        private fun ResultRow.toState() = State(
            idEquipment = this[StatesTable.equipmentId],
            state = this[StatesTable.state]
        )
    }
}

class TypeOfWorkDao {
    suspend fun getAllTypesOfWork(): List<TypeOfWork> =
        transaction {
            TypeOfWorkTable.selectAll()
                .map { it.toTypeOfWork() }
        }

    suspend fun getTypeOfWork(id: Int): TypeOfWork? =
        transaction {
            TypeOfWorkTable
                .select { TypeOfWorkTable.id eq id }
                .map { it.toTypeOfWork() }
                .singleOrNull()
        }

    suspend fun addTypeOfWork(name: String): TypeOfWork? =
        transaction {
            val insertStatement = TypeOfWorkTable.insert {
                it[TypeOfWorkTable.name] = name
            }
            insertStatement.resultedValues?.first()?.toTypeOfWork()
        }

    companion object {
        private fun ResultRow.toTypeOfWork() = TypeOfWork(
            idType = this[TypeOfWorkTable.id],
            nameType = this[TypeOfWorkTable.name]
        )
    }
}

class WorkerDao {
    suspend fun getAllWorkers(): List<Worker> =
        transaction {
            WorkersTable.selectAll()
                .map { it.toWorker() }
        }

    suspend fun getWorker(id: Int): Worker? =
        transaction {
            WorkersTable
                .select { WorkersTable.id eq id }
                .map { it.toWorker() }
                .singleOrNull()
        }

    suspend fun addWorker(name: String, contacts: String, position: String): Worker? =
        transaction {
            val insertStatement = WorkersTable.insert {
                it[WorkersTable.name] = name
                it[WorkersTable.contacts] = contacts
                it[WorkersTable.position] = position
            }
            insertStatement.resultedValues?.first()?.toWorker()
        }

    companion object {
        private fun ResultRow.toWorker() = Worker(
            idWorker = this[WorkersTable.id],
            nameWorker = this[WorkersTable.name],
            contactsWorker = this[WorkersTable.contacts],
            positionWorker = this[WorkersTable.position]
        )
    }
}

class ProductionDao {
    suspend fun getAllProductions(): List<Production> =
        transaction {
            ProductionTable.selectAll()
                .map { it.toProduction() }
        }

    suspend fun getProduction(id: Int): Production? =
        transaction {
            ProductionTable
                .select { ProductionTable.id eq id }
                .map { it.toProduction() }
                .singleOrNull()
        }

    suspend fun addProduction(idContents: Int, idWorker: Int, idEquipment: Int, dateOfWork: LocalDateTime, idTOW: Int): Production? =
        transaction {
            val insertStatement = ProductionTable.insert {
                it[equipmentId] = idEquipment
                it[workerId] = idWorker
                it[contentId] = idContents
                it[ProductionTable.dateOfWork] = dateOfWork
                it[typeOfWorkId] = idTOW
            }
            insertStatement.resultedValues?.first()?.toProduction()
        }

    companion object {
        private fun ResultRow.toProduction() = Production(
            idProduction = this[ProductionTable.id],
            idEquipment = this[ProductionTable.equipmentId],
            idWorker = this[ProductionTable.workerId],
            idContents = this[ProductionTable.contentId],
            dateOfWork = this[ProductionTable.dateOfWork],
            idTypeOfWork = this[ProductionTable.typeOfWorkId]
        )
    }
}
