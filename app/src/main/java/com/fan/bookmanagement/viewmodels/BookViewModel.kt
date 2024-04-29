package com.fan.bookmanagement.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fan.bookmanagement.api.BookApi
import com.fan.bookmanagement.data.Book
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class BookViewModel : ViewModel() {
    private val _bookList = MutableLiveData<List<Book>>()
    private val _errorMessage = MutableLiveData<String>()
    val bookList: LiveData<List<Book>> get() = _bookList
    val errorMessage: LiveData<String> = _errorMessage

    init {
        _bookList.value = emptyList()
    }

    private fun updateBookList(newList: List<Book>) {
        _bookList.value = newList
    }

    fun clearErrorMessage() {
        _errorMessage.postValue("")
    }

    fun fetchBooks() {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    BookApi.getAllBooks()
                }
                withContext(Dispatchers.Main) {
                    updateBookList(result)
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Failed to get book list: ${e.message}")
            }
        }
    }

    fun addBook(book: Book) {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    BookApi.addBook(book)
                }
                withContext(Dispatchers.Main) {
                    val currentList = _bookList.value.orEmpty().toMutableList()
                    currentList.add(result)
                    _bookList.postValue(currentList)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add book: ${e.message}"
            }
        }
    }

    fun deleteBook(id: Int) {
        try {
            viewModelScope.launch {
                val deleted = withContext(Dispatchers.IO) {
                    BookApi.deleteBook(id)
                }
                withContext(Dispatchers.Main) {
                    if (deleted) {
                        val newList = mutableListOf<Book>()
                        val currentList = _bookList.value.orEmpty().toMutableList()
                        currentList.forEach {
                            if (it.id != id) {
                                newList.add(it)
                            }
                        }
                        updateBookList(newList)
                    }
                }
            }
        } catch (e: Exception) {
            _errorMessage.postValue("Failed to delete book: ${e.message}")
        }
    }

    fun updateBook(id: Int, book: Book) {
        try {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    BookApi.updateBook(id, book)
                }
                withContext(Dispatchers.Main) {
                    val currentList = _bookList.value.orEmpty().toMutableList()
                    currentList.forEach {
                        if (it.id == id) {
                            it.title = book.title
                            it.author = book.author
                            it.year = book.year
                            it.isbn = book.isbn
                        }
                    }
                }
            }
        } catch (e: Exception) {
            _errorMessage.postValue("Failed to update book: ${e.message}")
        }
    }

}