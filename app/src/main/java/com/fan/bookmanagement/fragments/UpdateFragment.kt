package com.fan.bookmanagement.fragments

import android.content.Context
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import by.dzmitry_lakisau.month_year_picker_dialog.MonthYearPickerDialog
import com.fan.bookmanagement.R
import com.fan.bookmanagement.data.Book
import com.fan.bookmanagement.databinding.FragmentUpdateBinding
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException

private const val ARG_BOOK = "book"
class UpdateFragment : Fragment() {

    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!

    private var book: Book? = null

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
            book?.let { it1 -> updateBook(it1.id, updatedBook) }

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

    private fun updateBook(id: Int, book: Book) {
        val client = OkHttpClient()

        val requestBody =
            Gson().toJson(book).toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .addHeader("Content-Type", "application/json")
            .url("http://192.168.0.100:8080/books/${id}")
            .patch(requestBody)
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
                }
            }
        })
    }
}