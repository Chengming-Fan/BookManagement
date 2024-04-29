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
import com.fan.bookmanagement.databinding.FragmentUpdateBinding
import com.fan.bookmanagement.viewmodels.BookViewModel

private const val ARG_BOOK = "book"
class UpdateFragment : Fragment() {

    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!

    private var book: Book? = null
    private lateinit var bookViewModel: BookViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            book = it.getSerializable(ARG_BOOK) as Book
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUpdateBinding.inflate(inflater, container, false)
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
        binding.edittextTitle.setText(book?.title.toString())
        binding.edittextAuthor.setText(book?.author.toString())
        binding.edittextYear.setText(book?.year.toString())
        binding.edittextIsbn.setText(book?.isbn.toString())
        binding.edittextTitle.addTextChangedListener(textWatcher)
        binding.edittextAuthor.addTextChangedListener(textWatcher)
        binding.edittextYear.addTextChangedListener(textWatcher)
        binding.edittextIsbn.addTextChangedListener(textWatcher)

        binding.buttonUpdate.setOnClickListener { it ->
            val title = binding.edittextTitle.text.trim().toString()
            val author = binding.edittextAuthor.text.trim().toString()
            val year = binding.edittextYear.text.trim().toString()
            val isbn = binding.edittextIsbn.text.trim().toString()

            val updatedBook = Book(title, author, year.toInt(), isbn)
            book?.let { it1 -> bookViewModel.updateBook(it1.id, updatedBook) }

            val navController = Navigation.findNavController(it)
            navController.navigateUp()

            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
            binding.buttonUpdate.isEnabled = title.isNotEmpty() && author.isNotEmpty() &&
                    year.isNotEmpty() && isbn.isNotEmpty()
        }
    }

}