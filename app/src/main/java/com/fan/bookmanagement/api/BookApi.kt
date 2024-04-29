package com.fan.bookmanagement.api

import com.fan.bookmanagement.data.Book
import com.fan.bookmanagement.data.ErrorResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val API_URL = "http://18.167.54.212:8080/books"
private val gson = Gson()

object BookApi {

    suspend fun getAllBooks(): List<Book> {
        val client = OkHttpClient()

        val request = Request.Builder()
            .get()
            .url(API_URL)
            .build()

        return suspendCoroutine { continuation ->
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        handleBusinessException(response, continuation)
                    } else {
                        val responseData = response.body?.string()
                        val bookList: List<Book> =
                            gson.fromJson(responseData, object : TypeToken<List<Book>>() {}.type)
                        continuation.resume(bookList)
                    }
                }
            })
        }
    }

    suspend fun addBook(book: Book): Book {
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
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            handleBusinessException(response, continuation)
                        } else {
                            val responseData = response.body?.string()
                            continuation.resume(
                                gson.fromJson(
                                    responseData,
                                    object : TypeToken<Book>() {}.type
                                )
                            )
                        }
                    }
                }
            })
        }
    }

    suspend fun deleteBook(id: Int): Boolean {
        val client = OkHttpClient()

        val request = Request.Builder()
            .delete()
            .url("${API_URL}/${id}")
            .build()

        return suspendCoroutine { continuation ->
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) handleBusinessException(response, continuation)
                        else continuation.resume(true)
                    }
                }
            })
        }
    }

    suspend fun updateBook(id: Int, book: Book): Boolean {
        val client = OkHttpClient()

        val requestBody =
            gson.toJson(book).toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .addHeader("Content-Type", "application/json")
            .url("$API_URL/${id}")
            .patch(requestBody)
            .build()

        return suspendCoroutine { continuation ->
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) handleBusinessException(response, continuation)
                        else continuation.resume(true)
                    }
                }
            })
        }
    }

    private fun<T> handleBusinessException(
        response: Response,
        continuation: Continuation<T>
    ) {
        val responseData = response.body?.string()
        val errorResponse = gson.fromJson(responseData, ErrorResponse::class.java)
        continuation.resumeWithException(IOException(errorResponse.message))
    }
}