import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database

class DatabaseViewModel:ViewModel() {

    val modelRepository = ModelsDao()
    val materialDao = MaterialDao()
    val clientDao = ClientDao()
    val productionDao = ProductionDao()
    val cooRepository = ContentOfOrderDao()
    val equipmentDao = EquipmentDao()
    val mipRepository = MaterialInProductionDao()
    val orderDao = OrderDao()
    val stateDao = StateDao()
    val towRepository = TypeOfWorkDao()
    val workerDao = WorkerDao()

    val models = MutableStateFlow(emptyList<Model>())

    fun connect() {
        Database.connect(
            url = "jdbc:mysql://localhost:3306/watch_production",
            driver = "com.mysql.cj.jdbc.Driver",
            user = "root",
            password = "root"
        )
    }


    fun addModel(width: Int, length: Int, height: Int, desc: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                modelRepository.addModel(width, length, height, desc)
                models.update { modelRepository.getAllModels() }
            }
        }
    }

    fun updateModels(id: Int, width: Int, length: Int, height: Int, desc: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                modelRepository.updateModel(id, width, length, height, desc)
                models.update { modelRepository.getAllModels() }
            }
        }
    }

    fun deleteModels(id: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                modelRepository.deleteModel(id)
                models.update { modelRepository.getAllModels() }
            }
        }
    }

    fun fetchModels() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                models.update { modelRepository.getAllModels() }
            }
        }
    }
}