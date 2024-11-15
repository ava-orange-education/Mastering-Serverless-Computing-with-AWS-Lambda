package com.jamesthang.salerecords.menu

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.util.Calendar
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.jamesthang.salerecords.R
import com.jamesthang.salerecords.model.DTOOrderRecord
import com.jamesthang.salerecords.model.DTOProduct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
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
        viewModelScope.launch {
            _productList.value = fetchUserProducts()
        }
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
                it
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
            DTOProduct(id = UUID.randomUUID().toString(), name = "Black Coffee", price = 2.0, imageRes = R.drawable.ic_black_coffee, imageUrl = ""),
            DTOProduct(id = UUID.randomUUID().toString(), name = "Milk Coffee", price = 2.5, imageRes = R.drawable.ic_milk_coffee, imageUrl = ""),
            DTOProduct(id = UUID.randomUUID().toString(), name = "Hot Latte", price = 3.5, imageRes = R.drawable.ic_hot_latte, imageUrl = ""),
            DTOProduct(id = UUID.randomUUID().toString(), name = "Ice Latte", price = 3.5, imageRes = R.drawable.ic_ice_latte, imageUrl = ""),
            DTOProduct(id = UUID.randomUUID().toString(), name = "Matcha Latte", price = 4.5, imageRes = R.drawable.ic_macha_latte, imageUrl = ""),
            DTOProduct(id = UUID.randomUUID().toString(), name = "Walter", price = 1.0, imageRes = R.drawable.ic_walter, imageUrl = ""),
        )
    }

    private suspend fun fetchUserProducts(): List<DTOProduct> {
        val userId = auth.currentUser?.uid ?: return emptyList()

        return try {
            val snapshot = db.collection("users").document(userId).collection("products").get().await()
            snapshot.documents.mapNotNull { document ->
                val product = document.toObject(DTOProduct::class.java)
                product?.copy(id = document.id) // Ensure the id field is set from the document ID
            }
        } catch (e: Exception) {
            Log.e("Firebase", "Error fetching products: ${e.localizedMessage}")
            emptyList()
        }
    }

    fun uploadAllImagesToFirebaseStorage(context: Context) {
        val userId = auth.currentUser?.uid
        if (userId.isNullOrEmpty()) {
            Log.e("Firebase", "User ID is null")
            return
        }

        val productList = getMenuList()
        val productCount = productList.size
        var completedCount = 0

        productList.forEach { product ->
            uploadImageToFirebaseStorage(context, userId, product) { downloadURL ->
                if (downloadURL != null) {
                    writeProductDataToFirestore(userId, product, downloadURL)
                }
                completedCount++
                if (completedCount == productCount) {
                    // All uploads are done
                    Log.e("Firebase Storage", "Uploaded all images")
                }
            }
        }
    }

    private fun uploadImageToFirebaseStorage(context: Context, userId: String, product: DTOProduct, completion: (String?) -> Unit) {
        // Get the data from an ImageView as bytes
        val bitmap = BitmapFactory.decodeResource(context.resources, product.imageRes)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data = baos.toByteArray()

        // Create a unique file name
        val storageRef = FirebaseStorage.getInstance().reference.child("images/$userId/products/${product.id}.jpg")

        var uploadTask = storageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            Log.e("Firebase", "Error uploading image to Storage")
            completion(null)
        }.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                val downloadURL = uri.toString()
                Log.d("Firebase", "Image uploaded successfully. Download URL: $downloadURL")
                completion(downloadURL)
            }.addOnFailureListener { exception ->
                Log.e("Firebase", "Error getting download URL: ${exception.localizedMessage}")
                completion(null)
            }
        }
    }

    private fun writeProductDataToFirestore(userId: String, product: DTOProduct, imageUrl: String) {
        val productData = mapOf(
            "name" to product.name,
            "price" to product.price,
            "imageUrl" to imageUrl
        )

        db.collection("users").document(userId).collection("products").document(product.id)
            .set(productData)
            .addOnSuccessListener {
                Log.d("Firebase", "Product data successfully written to Firestore")
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Error writing product data to Firestore: ${exception.localizedMessage}")
            }
    }

}