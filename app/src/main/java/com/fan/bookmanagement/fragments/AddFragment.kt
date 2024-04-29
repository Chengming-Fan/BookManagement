package com.fan.bookmanagement.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import by.dzmitry_lakisau.month_year_picker_dialog.MonthYearPickerDialog
import com.fan.bookmanagement.R
import com.fan.bookmanagement.data.Book
import com.fan.bookmanagement.databinding.FragmentAddBinding
import com.fan.bookmanagement.viewmodels.BookViewModel

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var bookViewModel: BookViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookViewModel = ViewModelProvider(requireActivity()).get(BookViewModel::class.java)
        this.context?.let {
            val dialog = MonthYearPickerDialog.Builder(
                it,
                R.style.Style_MonthYearPickerDialog_Purple,
                selectedYear = resources.getInteger(R.integer.currentYear)
            )
                .setMinYear(resources.getInteger(R.integer.minYear))
                .setMode(MonthYearPickerDialog.Mode.YEAR_ONLY)
                .setOnYearSelectedListener { year ->
                    binding.edittextYear.setText(year.toString())
                }
                .build()
            binding.edittextYear.setOnClickListener {
                dialog.show()
            }
        }
        binding.buttonAdd.isEnabled = false
        binding.edittextTitle.requestFocus()
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.edittextTitle, InputMethodManager.SHOW_IMPLICIT)

        binding.edittextTitle.addTextChangedListener(textWatcher)
        binding.edittextAuthor.addTextChangedListener(textWatcher)
        binding.edittextYear.addTextChangedListener(textWatcher)
        binding.edittextIsbn.addTextChangedListener(textWatcher)

        binding.buttonAdd.setOnClickListener {
            val title = binding.edittextTitle.text.trim().toString()
            val author = binding.edittextAuthor.text.trim().toString()
            val year = binding.edittextYear.text.trim().toString()
            val isbn = binding.edittextIsbn.text.trim().toString()
            val book = Book(title, author, year.toInt(), isbn)
            bookViewModel.addBook(book)

            val navController = Navigation.findNavController(it)
            navController.navigateUp()

            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val title = binding.edittextTitle.text.trim()
            val author = binding.edittextAuthor.text.trim()
            val year = binding.edittextYear.text.trim()
            val isbn = binding.edittextIsbn.text.trim()
            binding.buttonAdd.isEnabled = title.isNotEmpty() && author.isNotEmpty() &&
                    year.isNotEmpty() && isbn.isNotEmpty()
        }
    }
}