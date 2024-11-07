package org.example.project

import DatabaseViewModel
import Model
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
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

        Column(Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally) {

            Row(modifier = Modifier.padding(16.dp)){
                Button(onClick = {
                    showDialog = true
                }) {
                    Text("Добавить запись")
                }

                if (showDialog) {
                    ParameterDialog(
                        onDismissRequest = {
                            showDialog = false
                        },
                        {width, length, height, desc ->
                            testvm.addModel(width, length, height, desc)}
                    )
                }

                Button(onClick = {
                    val last_id = models.last().idModel
                    testvm.deleteModels(last_id)

                }) {
                    Text("Удалить строчку")
                }

            }

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(models) { model ->
                    ModelRow(model, testvm::updateModels)
                }
            }
        }
    }
}

@Composable
fun ModelRow(model: Model, onUpdate: (id: Int, width: Int, length: Int, height: Int, desc: String) -> Unit){
    Row(modifier = Modifier.height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.SpaceEvenly){
        TableCell(model.idModel.toString(), .1f)
        EditableTableCell(
            text = model.widthModel.toString(),
            weight = .1f,
            onValueChange = { newValue -> onUpdate(model.idModel, newValue.toInt(), model.lengthModel, model.heightModel, model.descModel) },
            isNumeric = true
        )
        EditableTableCell(
            text = model.lengthModel.toString(),
            weight = .1f,
            onValueChange = { newValue -> onUpdate(model.idModel,  model.widthModel,  newValue.toInt(), model.heightModel, model.descModel) },
            isNumeric = true
        )
        EditableTableCell(
            text = model.heightModel.toString(),
            weight = .1f,
            onValueChange = { newValue -> onUpdate(model.idModel,  model.widthModel, model.lengthModel,  newValue.toInt(), model.descModel) },
            isNumeric = true
        )
        EditableTableCell(
            text = model.descModel,
            weight = .4f,
            onValueChange = { newValue -> onUpdate(model.idModel,  model.widthModel, model.lengthModel, model.heightModel,  newValue) },
            isNumeric = false
        )
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
fun RowScope.EditableTableCell(
    text: String,
    weight: Float,
    onValueChange: (String) -> Unit,
    isNumeric: Boolean
) {
    var isEditing by remember { mutableStateOf(false) }
    var value by remember { mutableStateOf(text) }

    if (isEditing) {
        TextField(
            value = value,
            onValueChange = { newValue -> if (!isNumeric || newValue.all { it.isDigit() }) {
                value = newValue
            }},
            modifier = Modifier
                .fillMaxHeight()
                .border(1.dp, Color.Black)
                .weight(weight)
                .padding(8.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = if (isNumeric) KeyboardType.Number else KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(onDone = {
                isEditing = false
                onValueChange(value)
            })
        )
    } else {
        Text(
            text = value,
            modifier = Modifier
                .fillMaxHeight()
                .border(1.dp, Color.Black)
                .weight(weight)
                .padding(8.dp)
                .clickable { isEditing = true }
        )
    }
}

@Composable
fun ParameterDialog(
    onDismissRequest: () -> Unit,
    onConfirm: ( Int, Int, Int, String) -> Unit,
) {
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
                            onConfirm(width.toInt(), length.toInt(), heigth.toInt(), desc)
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
