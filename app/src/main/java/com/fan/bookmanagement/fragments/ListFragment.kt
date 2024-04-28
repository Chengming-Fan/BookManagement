package com.fan.bookmanagement.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.fan.bookmanagement.R
import com.fan.bookmanagement.adapters.BookListAdapter
import com.fan.bookmanagement.data.Book
import com.fan.bookmanagement.databinding.FragmentListBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yanzhenjie.recyclerview.SwipeMenuCreator
import com.yanzhenjie.recyclerview.SwipeMenuItem
import com.yanzhenjie.recyclerview.SwipeRecyclerView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import org.json.JSONException

class ListFragment : Fragment(), MenuProvider {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var bookListAdapter: BookListAdapter
    val gson = Gson()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        _binding = FragmentListBinding.inflate(inflater, container, false)
        addSwipeMenu()
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.floatingAddButton.setOnClickListener {
            findNavController().navigate(R.id.action_fragment_list_to_fragment_add)
        }
        fetchBooks()
        bookListAdapter = BookListAdapter()

        val recyclerView: SwipeRecyclerView = binding.recyclerView
        recyclerView.adapter = bookListAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_main, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.app_bar_search -> {
                true
            }
            else -> false
        }
    }

    private fun addSwipeMenu() {
        val mSwipeMenuCreator =
            SwipeMenuCreator { leftMenu, rightMenu, position ->
                val updateItemRight = SwipeMenuItem(context)
                updateItemRight.setImage(R.drawable.baseline_create_24)
                updateItemRight.setWidth(120)
                updateItemRight.setHeight(120)
                val deleteItemRight = SwipeMenuItem(context)
                deleteItemRight.setImage(R.drawable.baseline_clear_24)
                deleteItemRight.setWidth(120)
                deleteItemRight.setHeight(120)
                rightMenu?.addMenuItem(updateItemRight)
                rightMenu?.addMenuItem(deleteItemRight)
            }
        binding.recyclerView.setSwipeMenuCreator(mSwipeMenuCreator)
        binding.recyclerView.setOnItemMenuClickListener { menuBridge, adapterPosition ->
            menuBridge.closeMenu()
            when (menuBridge.position) {
                0 -> {
                    val bundle = bundleOf("book" to bookListAdapter.getData()[adapterPosition])
                    findNavController().navigate(R.id.action_fragment_list_to_fragment_update, bundle)
                }
                1 -> showDeleteConfirmDialog(bookListAdapter.getData()[adapterPosition])
                else -> {}
            }
        }
    }

    private fun showDeleteConfirmDialog(book: Book) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete")
            .setMessage("Are you sure to delete this book?")
            .setPositiveButton("Yes") { dialog, which ->
                deleteBook(book.id)
            }
            .show()
    }

    private fun fetchBooks() {
        val client = OkHttpClient()

        val request = Request.Builder()
            .get()
            .url("http://192.168.0.100:8080/books")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Looper.prepare()
                Toast.makeText(context, "Failed to call API, please try later", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responseData = response.body?.string()

                    try {
                        val bookList: List<Book> = gson.fromJson(responseData, object : TypeToken<List<Book>>() {}.type)
                        bookListAdapter.setData(bookList)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }

    private fun deleteBook(id: Int) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .delete()
            .url("http://192.168.0.100:8080/books/${id}")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Looper.prepare()
                Toast.makeText(context, "Failed to call API, please try later", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    fetchBooks()
                }
            }
        })
    }

}