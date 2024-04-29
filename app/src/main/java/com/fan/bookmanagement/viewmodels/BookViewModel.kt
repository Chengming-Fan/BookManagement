package com.fan.bookmanagement.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fan.bookmanagement.data.Book
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BookViewModel : ViewModel() {
    private val _bookList = MutableLiveData<List<Book>>()
    private val _errorMessage = MutableLiveData<String>()
    val bookList: LiveData<List<Book>> get() = _bookList
    val errorMessage: LiveData<String> = _errorMessage

    init {
        _bookList.value = emptyList() // 初始化空的书籍列表
    }

    fun updateBookList(newList: List<Book>) {
        _bookList.value = newList
    }

    fun addBook(book: Book) {
        val currentList = _bookList.value.orEmpty().toMutableList()
        currentList.add(book)
        _bookList.postValue(currentList)
    }

    fun fetchData() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                fetchBooks()
            }
            withContext(Dispatchers.Main) {
                updateBookList(result)
            }
        }
    }

    private suspend fun fetchBooks(): List<Book> {
        val client = OkHttpClient()

        val request = Request.Builder()
            .get()
            .url("http://192.168.0.100:8080/books")
            .build()

        return suspendCoroutine { continuation ->
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    _errorMessage.postValue("Network Request Failed")
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseData = response.body?.string()
                    var gson = Gson()
                    val bookList: List<Book> =
                        gson.fromJson(responseData, object : TypeToken<List<Book>>() {}.type)
                    continuation.resume(bookList)
                }
            })
        }
//        client.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                Looper.prepare()
//                Toast.makeText(context, "Failed to call API, please try later", Toast.LENGTH_LONG).show()
//                e.printStackTrace()
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                response.use {
//                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
//
//                    val responseData = response.body?.string()
//
//                    try {
//                        val bookList: List<Book> = gson.fromJson(responseData, object : TypeToken<List<Book>>() {}.type)
//                        bookListAdapter.setData(bookList)
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                    }
//                }
//            }
//        })
    }

//    private fun callAddBook(book: Book) {
//        val client = OkHttpClient()
//
//        val requestBody =
//            Gson().toJson(book).toRequestBody("application/json".toMediaTypeOrNull())
//        val request = Request.Builder()
//            .addHeader("Content-Type", "application/json")
//            .url("http://192.168.0.100:8080/books")
//            .post(requestBody)
//            .build()
//
//        client.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                Looper.prepare()
//                Toast.makeText(context, "Failed to call API, please try later", Toast.LENGTH_LONG).show()
//                e.printStackTrace()
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                response.use {
//                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
//                }
//            }
//        })
//    }
}