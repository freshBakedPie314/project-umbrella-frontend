package com.example.project_umbrella.composables

import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project_umbrella.viewModel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropDownMenu(
    viewModel : GameViewModel
) {

    val filterOptions = listOf("Most Played", "Highest Rated", "Recently Released")
    var isExpanded by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf(filterOptions[0]) }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it }
    ) {
        TextField(
            value = "Sorted by $selectedFilter",
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
           modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )

        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {

            filterOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text("Sort by $option") },
                    onClick = {
                        selectedFilter = option
                        isExpanded = false
                        viewModel.onSortingChanged(selectedFilter)
                    }
                )
            }
        }
    }
}