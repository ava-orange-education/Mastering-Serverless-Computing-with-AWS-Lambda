package com.jamesthang.salerecords.menu

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jamesthang.salerecords.R
import com.jamesthang.salerecords.model.DTOOrderRecord
import com.jamesthang.salerecords.model.DTOProduct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _orderRecords: MutableStateFlow<List<DTOOrderRecord>> =
        MutableStateFlow(emptyList())
    val orderRecords = _orderRecords.asStateFlow()

    private val _productList: MutableStateFlow<List<DTOProduct>> = MutableStateFlow(emptyList())
    val productList = _productList.asStateFlow()

    init {
        _productList.value = getMenuList()
    }

    fun onProductClick(dtoProduct: DTOProduct) {
        val selectedProduct = _orderRecords.value.find {
            it.id == dtoProduct.id
        }

        if (selectedProduct == null) {
            _orderRecords.value += DTOOrderRecord(
                id = dtoProduct.id,
                name = dtoProduct.name,
                price = dtoProduct.price,
                quantity = 1
            )
        } else {
            _orderRecords.value = _orderRecords.value.map {
                if (it.id == selectedProduct.id) {
                    it.copy(quantity = selectedProduct.quantity + 1)
                } else {
                    it
                }
            }
        }
    }

    fun onOrderClick() {
        val orderRecords = _orderRecords.value
        val userId = auth.currentUser?.uid

        if (orderRecords.isEmpty() || userId.isNullOrEmpty()) {
            return
        }

        val salesData = hashMapOf(
            "date" to Calendar.getInstance().timeInMillis / 1000,
            "sale" to orderRecords.sumOf { it.total() },
            "products" to orderRecords.map {
                hashMapOf(
                    "productId" to it.id,
                    "productName" to it.name,
                    "quantity" to it.quantity,
                    "subtotal" to it.total(),
                    "unitPrice" to it.price
                )
            },
        )

        db.collection("users").document(userId).collection("sales")
            .add(salesData)
            .addOnSuccessListener {
                // Clear all order records
                _orderRecords.value = emptyList()
            }
            .addOnFailureListener {
                // Handle notification here
            }
    }

    fun onOrderRemoveClick(dtoOrderRecord: DTOOrderRecord) {
        val selected = _orderRecords.value.find { it.id == dtoOrderRecord.id }
        if (selected != null) {
            _orderRecords.value -= selected
        }
    }

    private fun getMenuList(): List<DTOProduct> {
        return listOf(
            DTOProduct(name = "Black Coffee", price = 2.0, imageRes = R.drawable.ic_black_coffee),
            DTOProduct(name = "Milk Coffee", price = 2.5, imageRes = R.drawable.ic_milk_coffee),
            DTOProduct(name = "Hot Latte", price = 3.5, imageRes = R.drawable.ic_hot_latte),
            DTOProduct(name = "Ice Latte", price = 3.5, imageRes = R.drawable.ic_ice_latte),
            DTOProduct(name = "Matcha Latte", price = 4.5, imageRes = R.drawable.ic_macha_latte),
            DTOProduct(name = "Walter", price = 1.0, imageRes = R.drawable.ic_walter),
        )
    }
}