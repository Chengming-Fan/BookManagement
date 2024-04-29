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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val API_URL = "http://192.168.0.100:8080/books"
private const val BAD_REQUEST = 400
private val gson = Gson()

class BookViewModel : ViewModel() {
    private val _bookList = MutableLiveData<List<Book>>()
    private val _errorMessage = MutableLiveData<String>()
    val bookList: LiveData<List<Book>> get() = _bookList
    val errorMessage: LiveData<String> = _errorMessage

    init {
        _bookList.value = emptyList()
    }

    fun updateBookList(newList: List<Book>) {
        _bookList.value = newList
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

    fun addBook(book: Book) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                callAddBookApi(book)
            }
            withContext(Dispatchers.Main) {
                val currentList = _bookList.value.orEmpty().toMutableList()
                currentList.add(result)
                _bookList.postValue(currentList)
            }
        }
    }

    private suspend fun fetchBooks(): List<Book> {
        val client = OkHttpClient()

        val request = Request.Builder()
            .get()
            .url(API_URL)
            .build()

        return suspendCoroutine { continuation ->
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    _errorMessage.postValue("Network Request Failed")
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseData = response.body?.string()
                    val bookList: List<Book> =
                        gson.fromJson(responseData, object : TypeToken<List<Book>>() {}.type)
                    continuation.resume(bookList)
                }
            })
        }
    }

    private suspend fun callAddBookApi(book: Book): Book {
        val client = OkHttpClient()

        val requestBody =
            gson.toJson(book).toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .addHeader("Content-Type", "application/json")
            .url(API_URL)
            .post(requestBody)
            .build()

        return suspendCoroutine { continuation ->
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    _errorMessage.postValue("Add Failed")
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (response.code == BAD_REQUEST) {
                            _errorMessage.postValue("isbn repeat")
                        }
                        val responseData = response.body?.string()
                        continuation.resume(gson.fromJson(responseData, object : TypeToken<Book>() {}.type))
                    }
                }
            })
        }
    }
}