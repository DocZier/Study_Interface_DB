package org.example.project

import DatabaseViewModel
import Model
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    val testvm = DatabaseViewModel()
    testvm.connect()
    MaterialTheme {
        Scaffold(modifier = Modifier,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Models"
                        )
                    })
            }) { paddingValues ->
            val models by testvm.models.collectAsStateWithLifecycle()
            testvm.fetchModels()
            var showDialog by remember { mutableStateOf(false) }

            var sortByColumn by remember { mutableStateOf<SortColumn?>(null) }
            var sortAscending by remember { mutableStateOf(true) }

            var selectedFilterColumn by remember { mutableStateOf<SortColumn?>(null) }
            var filterQuery by remember { mutableStateOf("") }
            var filterOperation by remember { mutableStateOf(FilterOperation.Contains) }

            Column(
                Modifier.padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Spacer(Modifier.width(8.dp))

                    DropdownMenuFilter(selectedFilterColumn) {
                        selectedFilterColumn = it
                    }
                    Spacer(Modifier.width(8.dp))

                    FilterOperationMenu(selectedFilterColumn, filterOperation) {
                        filterOperation = it
                    }
                    Spacer(Modifier.width(8.dp))

                    TextField(
                        value = filterQuery,
                        onValueChange = { filterQuery = it },
                        label = { Text("Условие фильтра") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.padding(16.dp)) {
                    Button(onClick = {
                        showDialog = true
                    }) {
                        Text("Добавить запись")
                    }

                    if (showDialog) {
                        AddModelDialog(
                            onDismissRequest = {
                                showDialog = false
                            },
                            { width, length, height, desc ->
                                testvm.addModel(width, length, height, desc)
                            }
                        )
                    }

                    Button(onClick = {
                        val lastId = models.last().idModel
                        testvm.deleteModels(lastId)
                    }) {
                        Text("Удалить строчку")
                    }

                }

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        Row(
                            modifier = Modifier.height(IntrinsicSize.Min),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TableHeader("ID", .1f, "idModel", sortByColumn, sortAscending) {
                                sortByColumn = SortColumn.Id
                                sortAscending = !sortAscending
                            }
                            TableHeader("Ширина", .1f, "widthModel", sortByColumn, sortAscending) {
                                sortByColumn = SortColumn.Width
                                sortAscending = !sortAscending
                            }
                            TableHeader("Длина", .1f, "lengthModel", sortByColumn, sortAscending) {
                                sortByColumn = SortColumn.Length
                                sortAscending = !sortAscending
                            }
                            TableHeader("Высота", .1f, "heightModel", sortByColumn, sortAscending) {
                                sortByColumn = SortColumn.Height
                                sortAscending = !sortAscending
                            }
                            TableHeader("Описание", .4f, "descModel", sortByColumn, sortAscending) {
                                sortByColumn = SortColumn.Description
                                sortAscending = !sortAscending
                            }
                        }
                    }

                    items(filterModels(sortModels(models, sortByColumn, sortAscending), selectedFilterColumn, filterOperation, filterQuery)) { model ->
                        ModelRow(model, testvm::updateModels)
                    }
                }
            }
        }
    }
}

enum class SortColumn {
    Id, Width, Length, Height, Description
}

@Composable
fun RowScope.TableHeader(
    text: String,
    weight: Float,
    columnName: String,
    sortByColumn: SortColumn?,
    sortAscending: Boolean,
    onClick: () -> Unit
) {
    val isSelected = when (columnName) {
        "idModel" -> sortByColumn == SortColumn.Id
        "widthModel" -> sortByColumn == SortColumn.Width
        "lengthModel" -> sortByColumn == SortColumn.Length
        "heightModel" -> sortByColumn == SortColumn.Height
        "descModel" -> sortByColumn == SortColumn.Description
        else -> false
    }

    Text(
        text = text,
        modifier = Modifier
            .fillMaxHeight()
            .border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp)
            .clickable { onClick() },
        style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    )
}

fun sortModels(models: List<Model>, sortByColumn: SortColumn?, sortAscending: Boolean): List<Model> {
    return when (sortByColumn) {
        SortColumn.Id -> if (sortAscending) models.sortedBy { it.idModel } else models.sortedByDescending { it.idModel }
        SortColumn.Width -> if (sortAscending) models.sortedBy { it.widthModel } else models.sortedByDescending { it.widthModel }
        SortColumn.Length -> if (sortAscending) models.sortedBy { it.lengthModel } else models.sortedByDescending { it.lengthModel }
        SortColumn.Height -> if (sortAscending) models.sortedBy { it.heightModel } else models.sortedByDescending { it.heightModel }
        SortColumn.Description -> if (sortAscending) models.sortedBy { it.descModel } else models.sortedByDescending { it.descModel }
        else -> models
    }
}

// Enum для операций фильтрации
enum class FilterOperation(val label: String) {
    Less("<"),
    LessOrEqual("<="),
    Greater(">"),
    GreaterOrEqual(">="),
    Equals("=="),
    NotEquals("!="),
    Contains("Содержит"),
    NotContains("Не содержит")
}

// Функция для фильтрации данных
fun filterModels(
    models: List<Model>,
    filterColumn: SortColumn?,
    operation: FilterOperation,
    query: String
): List<Model> {
    if (query.isBlank() || filterColumn == null) return models

    return models.filter { model ->
        val valueToFilter = when (filterColumn) {
            SortColumn.Id -> model.idModel.toString()
            SortColumn.Width -> model.widthModel.toString()
            SortColumn.Length -> model.lengthModel.toString()
            SortColumn.Height -> model.heightModel.toString()
            SortColumn.Description -> model.descModel
        }

        val numericQuery = query.toIntOrNull()
        when (operation) {
            FilterOperation.Less -> numericQuery != null && valueToFilter.toInt() < numericQuery
            FilterOperation.LessOrEqual -> numericQuery != null && valueToFilter.toInt() <= numericQuery
            FilterOperation.Greater -> numericQuery != null && valueToFilter.toInt() > numericQuery
            FilterOperation.GreaterOrEqual -> numericQuery != null && valueToFilter.toInt() >= numericQuery
            FilterOperation.Equals -> valueToFilter == query
            FilterOperation.NotEquals -> valueToFilter != query
            FilterOperation.Contains -> valueToFilter.contains(query, ignoreCase = true)
            FilterOperation.NotContains -> !valueToFilter.contains(query, ignoreCase = true)
        }
    }
}

@Composable
fun FilterOperationMenu(selectedColumn: SortColumn?, selectedOperation: FilterOperation, onOperationSelected: (FilterOperation) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Text(
            text = selectedOperation.label,
            modifier = Modifier
                .clickable { expanded = true }
                .padding(8.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline, shape = MaterialTheme.shapes.small)
                .padding(8.dp)
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            val operations = if (selectedColumn == SortColumn.Description) {
                listOf(FilterOperation.Contains, FilterOperation.NotContains)
            } else {
                FilterOperation.entries
            }
            operations.forEach { operation ->
                DropdownMenuItem(
                    text = { Text(operation.label) },
                    onClick = {
                        onOperationSelected(operation)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun DropdownMenuFilter(selectedFilterColumn: SortColumn?, onColumnSelected: (SortColumn) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Text(
            text = selectedFilterColumn?.name ?: "Выберите колонку",
            modifier = Modifier
                .clickable { expanded = true }
                .padding(8.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline, shape = MaterialTheme.shapes.small)
                .padding(8.dp)
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            SortColumn.entries.forEach { column ->
                DropdownMenuItem(
                    text = { Text(column.name) },
                    onClick = {
                        onColumnSelected(column)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ModelRow(model: Model, onUpdate: (id: Int, width: Int, length: Int, height: Int, desc: String) -> Unit){
    Row(modifier = Modifier.height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.SpaceEvenly){
        EditableTableCell(
            text = model.idModel.toString(),
            weight = .1f,
            onValueChange = { },
            isNumeric = true,
            isChangeable = false
        )
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
fun RowScope.EditableTableCell(
    text: String,
    weight: Float,
    onValueChange: (String) -> Unit,
    isNumeric: Boolean,
    isChangeable: Boolean = true
) {
    var isEditing by remember { mutableStateOf(false) }
    var value by remember { mutableStateOf(TextFieldValue(text)) }
    val focusRequester = remember { FocusRequester() }


    if (isEditing && isChangeable) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
            value = value.copy(selection = TextRange(value.text.length))
        }
        BasicTextField(
            value = value,
            onValueChange = { newValue -> if (!isNumeric || newValue.text.all { it.isDigit() }) {
                value = newValue
            }},
            modifier = Modifier
                .fillMaxHeight()
                .border(1.dp, Color.Black)
                .weight(weight)
                .padding(8.dp)
                .focusRequester(focusRequester)
            ,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = if (isNumeric) KeyboardType.Number else KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(onDone = {
                isEditing = false
                val finalValue = if (isNumeric && value.text.isEmpty()) "0" else value.text
                onValueChange(finalValue)
            })
        )
    } else {
        Text(
            text = text,
            modifier = Modifier
                .fillMaxHeight()
                .border(1.dp, Color.Black)
                .weight(weight)
                .padding(8.dp)
                .clickable {
                    isEditing = true
                    value = TextFieldValue(text)
                }
        )
    }
}

@Composable
fun AddModelDialog(
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
