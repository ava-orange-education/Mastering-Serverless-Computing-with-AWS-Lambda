package com.jamesthang.salerecords.records

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jamesthang.salerecords.model.DTOSaleRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RecordsViewModel @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _saleRecords: MutableStateFlow<List<DTOSaleRecord>> =
        MutableStateFlow(emptyList())
    val saleRecords = _saleRecords.asStateFlow()

    init {
        listenSaleRecordChanges()
    }

    private fun listenSaleRecordChanges() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId)
            .collection("sales")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                val newList = value?.documents?.map {
                    val saleRecord =
                        it.toObject(DTOSaleRecord::class.java) ?: return@addSnapshotListener
                    saleRecord.id = it.id
                    saleRecord
                } ?: emptyList()
                _saleRecords.value = newList
            }
    }

    fun onRemoveClick(dtoSaleRecord: DTOSaleRecord) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId)
            .collection("sales")
            .document(dtoSaleRecord.id)
            .delete()
    }

}