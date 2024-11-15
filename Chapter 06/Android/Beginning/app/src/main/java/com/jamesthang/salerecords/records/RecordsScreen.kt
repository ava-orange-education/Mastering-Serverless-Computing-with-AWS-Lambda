package com.jamesthang.salerecords.records

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.gson.Gson
import com.jamesthang.salerecords.AppScreen
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordsScreen(navController: NavController, viewModel: RecordsViewModel = hiltViewModel()) {
    val saleRecords = viewModel.saleRecords.collectAsState()
    val sortEnums = viewModel.sortEnums.collectAsState()
    val selectedSort = viewModel.selectedSortEnum.collectAsState()
    val selectedDate = viewModel.selectedDate.collectAsState()

    val bottomSheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = SheetState(
            skipPartiallyExpanded = false,
            density = Density(density = 1f),
            initialValue = SheetValue.Hidden
        )
    )
    val scope = rememberCoroutineScope()
    var selectedDateStr by remember { mutableStateOf("") }

    val datePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _, year, monthOfYear, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, monthOfYear, dayOfMonth)

            viewModel.onDateClick(selectedCalendar)
        },
        selectedDate.value.get(Calendar.YEAR),
        selectedDate.value.get(Calendar.MONTH),
        selectedDate.value.get(Calendar.DAY_OF_MONTH)
    )

    LaunchedEffect(selectedDate.value) {
        val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        selectedDateStr = format.format(selectedDate.value.time)
    }

    BottomSheetScaffold(
        scaffoldState = bottomSheetState,
        sheetContent = {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .wrapContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Sort By", textAlign = TextAlign.Center, fontSize = 14.sp)
                Text("Select sort option", textAlign = TextAlign.Center, fontSize = 12.sp)

                for (sort in sortEnums.value) {
                    Button(
                        onClick = {
                            viewModel.onSortClick(sort)
                            scope.launch {
                                bottomSheetState.bottomSheetState.hide()
                            }
                        }, modifier = Modifier
                            .padding(top = 6.dp)
                            .fillMaxWidth()
                    ) {
                        Text(sort.title)
                    }
                }
            }
        },
        sheetPeekHeight = 0.dp
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = selectedSort.value.title, modifier = Modifier.clickable {
                        scope.launch {
                            bottomSheetState.bottomSheetState.expand()
                        }
                    })

                    Text(
                        text = selectedDateStr,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(color = Color.Gray)
                            .padding(horizontal = 5.dp, vertical = 3.dp)
                            .clickable {
                                datePickerDialog.show()
                            }
                    )
                }
            }
            if (saleRecords.value.isNotEmpty()) {
                itemsIndexed(saleRecords.value) { index, dtoSaleRecord ->
                    RecordItemView(index = index + 1, model = dtoSaleRecord, onRemoveClick = {
                        viewModel.onRemoveClick(it)
                    }, onItemClick = {
                        val saleRecordJson = Gson().toJson(it)
                        navController.navigate(AppScreen.RecordDetailScreen.route + "/$saleRecordJson")
                    })
                }
            }
        }
    }
}