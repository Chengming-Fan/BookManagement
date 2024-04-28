package com.fan.bookmanagement.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.fan.bookmanagement.R
import com.fan.bookmanagement.adapters.BookListAdapter
import com.fan.bookmanagement.databinding.FragmentListBinding

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.floatingAddButton.setOnClickListener {
            findNavController().navigate(R.id.action_fragment_list_to_fragment_add)
        }
        val dataset = arrayOf("Java", "Python", "Rust", "C++", "C#", "C", "kotlin", "scala", "JavaScript", "html", "Golang", "PHP")
        val bookListAdapter = BookListAdapter(dataset)

        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.adapter = bookListAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}