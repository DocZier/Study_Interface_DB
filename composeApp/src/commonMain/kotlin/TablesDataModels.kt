import androidx.compose.runtime.Immutable
import java.time.LocalDateTime

@Immutable
data class Client(
    val idClient: Int,
    val nameClient: String,
    val contactsClient: String
)

@Immutable
data class ContentOfOrder(
    val idContents: Int,
    val idOrder: Int,
    val idModel: Int,
    val count: Int
)

@Immutable
data class Equipment(
    val idEquipment: Int,
    val nameEquipment: String,
    val descEquipment: String
)

@Immutable
data class Material(
    val idMaterial: Int,
    val nameMaterial: String,
    val descMaterial: String
)

@Immutable
data class MaterialInProduction(
    val idMaterial: Int,
    val idProduction: Int,
    val count: Int
)

@Immutable
data class Model(
    val idModel: Int,
    val widthModel: Int,
    val lengthModel: Int,
    val heightModel: Int,
    val descModel: String
){
    override fun toString(): String {
        return "$idModel " +
                "$widthModel " +
                "$lengthModel " +
                "$heightModel " +
                descModel
    }
}

@Immutable
data class Order(
    val idOrder: Int,
    val dateOfFormation: LocalDateTime,
    val deadline: LocalDateTime,
    val cost: Int,
    val idClient: Int,
    val delAddress: String?
)

@Immutable
data class Production(
    val idProduction: Int,
    val idContents: Int,
    val dateOfWork: LocalDateTime,
    val idWorker: Int,
    val idEquipment: Int,
    val idTypeOfWork: Int
)

@Immutable
data class State(
    val idEquipment: Int,
    val state: String
)

@Immutable
data class TypeOfWork(
    val idType: Int,
    val nameType: String
)

@Immutable
data class Worker(
    val idWorker: Int,
    val nameWorker: String,
    val positionWorker: String,
    val contactsWorker: String
)
