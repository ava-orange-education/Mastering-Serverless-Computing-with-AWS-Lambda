package com.jamesthang.salerecords.records

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jamesthang.salerecords.model.DTOSaleRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class RecordsViewModel @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _saleRecords: MutableStateFlow<List<DTOSaleRecord>> =
        MutableStateFlow(emptyList())
    val saleRecords = _saleRecords.asStateFlow()

    private val _sortEnums: MutableStateFlow<List<SortEnum>> =
        MutableStateFlow(emptyList())
    val sortEnums = _sortEnums.asStateFlow()

    private val _selectedSortEnum: MutableStateFlow<SortEnum> =
        MutableStateFlow(SortEnum.TimeAscending)
    val selectedSortEnum = _selectedSortEnum.asStateFlow()

    private val _selectedDate: MutableStateFlow<Calendar> =
        MutableStateFlow(Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        })
    val selectedDate = _selectedDate.asStateFlow()

    private var lastDocumentSnapshot: DocumentSnapshot? = null
    var listState by mutableStateOf(ListState.IDLE)

    init {
        _sortEnums.value = listOf(
            SortEnum.TimeAscending,
            SortEnum.TimeDescending,
            SortEnum.SaleAscending,
            SortEnum.SaleDescending,
        )

        viewModelScope.launch {
            combine(_selectedDate, _selectedSortEnum) { date, enum ->
                Pair(date, enum)
            }.collectLatest { _ ->
                loadInitialSales()
            }
        }
    }

    fun loadInitialSales() {
        lastDocumentSnapshot = null
        listenSaleRecordChanges(reset = true)
    }

    fun loadMoreSales() {
        listenSaleRecordChanges(reset = false)
    }

    private fun listenSaleRecordChanges(reset: Boolean) {
        val userId = auth.currentUser?.uid ?: return
        listState = ListState.LOADING

        val salesRef = FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection("sales")

        val todayStart = _selectedDate.value.time

        val todayEnd = Calendar.getInstance().apply {
            time = todayStart
            add(Calendar.DAY_OF_MONTH, 1)
        }.time

        val filterOptions = _selectedSortEnum.value.getFilterOption()
        val sortBy = filterOptions.first
        val descending = filterOptions.second

        var query = salesRef
            .whereGreaterThanOrEqualTo("date", todayStart.time / 1000)
            .whereLessThan("date", todayEnd.time / 1000)
            .orderBy(
                sortBy,
                if (descending) Query.Direction.DESCENDING else Query.Direction.ASCENDING
            )
            .limit(15)

        if (!reset) {
            lastDocumentSnapshot?.let {
                query = query.startAfter(it)
            }
        }

        query.get().addOnSuccessListener { snapshot ->
            val newList = mutableListOf<DTOSaleRecord>()
            for (document in snapshot.documents) {
                val dtoSaleRecord = document.toObject(DTOSaleRecord::class.java)?.apply {
                    id = document.id
                } ?: continue
                newList.add(dtoSaleRecord)
            }

            if (newList.isNotEmpty()) {
                if (reset) {
                    _saleRecords.value = newList
                } else {
                    _saleRecords.value += newList
                }
            }
            listState = ListState.IDLE
            lastDocumentSnapshot = snapshot.documents.lastOrNull()
        }.addOnFailureListener { error ->
            println("Error fetching snapshots: $error")
        }
    }

    fun onRemoveClick(dtoSaleRecord: DTOSaleRecord) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId)
            .collection("sales")
            .document(dtoSaleRecord.id)
            .delete().addOnSuccessListener {
                val newList = _saleRecords.value.toMutableList()
                newList.removeIf { it.id == dtoSaleRecord.id }
                _saleRecords.value = newList
            }
    }

    fun onSortClick(sortEnum: SortEnum) {
        _selectedSortEnum.value = sortEnum
    }

    fun onDateClick(date: Calendar) {
        _selectedDate.value = date.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
    }
}

internal fun LazyListState.reachedBottom(buffer: Int = 1): Boolean {
    val lastVisibleItem = this.layoutInfo.visibleItemsInfo.lastOrNull()
    return lastVisibleItem?.index != 0 && lastVisibleItem?.index == this.layoutInfo.totalItemsCount - buffer
}

enum class ListState {
    IDLE,
    LOADING
}