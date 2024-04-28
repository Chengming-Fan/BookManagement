package com.fan.bookmanagement.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.fan.bookmanagement.R
import com.fan.bookmanagement.adapters.BookListAdapter
import com.fan.bookmanagement.databinding.FragmentListBinding
import com.yanzhenjie.recyclerview.SwipeMenuCreator
import com.yanzhenjie.recyclerview.SwipeMenuItem
import com.yanzhenjie.recyclerview.SwipeRecyclerView

class ListFragment : Fragment(), MenuProvider {

    private var _binding: FragmentListBinding? = null

    private val binding get() = _binding!!

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
        val dataset = arrayOf("Java", "Python", "Rust", "C++", "C#", "C", "kotlin", "scala", "JavaScript", "html", "Golang", "PHP")
        val bookListAdapter = BookListAdapter(dataset)

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
                0 -> findNavController().navigate(R.id.action_fragment_list_to_fragment_update)
                1 -> showDeleteConfirmDialog()
                else -> {}
            }
        }
    }

    private fun showDeleteConfirmDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete")
            .setMessage("Are you sure to delete this book?")
            .setPositiveButton("Yes") { dialog, which ->
            }
            .setNegativeButton("No") { dialog, which ->
            }
            .show()
    }
}