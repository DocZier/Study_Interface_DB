package org.example.project

import DatabaseViewModel
import Model
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val testvm = DatabaseViewModel()
    testvm.connect()
    MaterialTheme {
        val models by testvm.models.collectAsStateWithLifecycle()
        testvm.fetchModels()
        var showDialog by remember { mutableStateOf(false) }
        var isUpdate by remember { mutableStateOf(false) }

        Column(Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally) {

            Row(modifier = Modifier.padding(16.dp)){
                Button(onClick = {
                    showDialog = true
                }) {
                    Text("Добавить запись")
                }

                Button(onClick = {
                    showDialog = true
                    isUpdate = true
                }) {
                    Text("Изменить запись")
                }

                if (showDialog) {
                    ParameterDialog(
                        onDismissRequest = {
                            showDialog = false
                            isUpdate = false
                        },
                        onConfirm = if(isUpdate)
                            {id, width, length, height, desc ->
                                testvm.updateModels(id, width, length, height, desc)}
                        else
                            {id, width, length, height, desc ->
                                testvm.addModel(width, length, height, desc)},
                        isUpdate
                    )
                }

                Button(onClick = {
                    val last_id = models.last().idModel
                    testvm.deleteModels(last_id)

                }) {
                    Text("Удалить строчку")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(models) { model ->
                    ModelRow(model)
                }
            }
        }
    }
}

@Composable
fun ModelRow(model: Model){
    Row(modifier = Modifier.height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.SpaceEvenly){
        TableCell(model.idModel.toString(), .1f)
        TableCell(model.widthModel.toString(), .1f)
        TableCell(model.lengthModel.toString(), .1f)
        TableCell(model.heightModel.toString(), .1f)
        TableCell(model.descModel, .4f)
    }
}

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float
) {
    Text(
        text = text,
        modifier = Modifier.fillMaxHeight()
            .border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp)
    )
}

@Composable
fun ParameterDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (Int, Int, Int, Int, String) -> Unit,
    isUpdating: Boolean = false
) {
    var id by remember { mutableStateOf("0") }
    var width by remember { mutableStateOf("0") }
    var length by remember { mutableStateOf("0") }
    var heigth by remember { mutableStateOf("0") }
    var desc by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Surface(
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Введите информацию о модели")

                if (isUpdating)
                    OutlinedTextField(
                        value = id,
                        onValueChange = { newValue ->
                            id = newValue.filter { it.isDigit() }
                        },
                        label = { Text("ID:") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                OutlinedTextField(
                    value = width,
                    onValueChange = { newValue ->
                        width = newValue.filter { it.isDigit() }
                    },
                    label = { Text("Ширина:") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = length,
                    onValueChange = { newValue ->
                        length = newValue.filter { it.isDigit() }
                    },
                    label = { Text("Длина:") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = heigth,
                    onValueChange = { newValue ->
                        heigth = newValue.filter { it.isDigit() }
                    },
                    label = { Text("Высота:") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Описание:") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = { onDismissRequest() }) {
                        Text("Назад")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = {
                            onConfirm(id.toInt(), width.toInt(), length.toInt(), heigth.toInt(), desc)
                            onDismissRequest()
                        }
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}
