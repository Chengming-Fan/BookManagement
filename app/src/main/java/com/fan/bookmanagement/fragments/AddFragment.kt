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
import com.fan.bookmanagement.databinding.FragmentAddBinding
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddBinding.inflate(inflater, container, false)
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
            addBook(book)

            val navController = Navigation.findNavController(it)
            navController.navigateUp()

            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun addBook(book: Book) {
        val client = OkHttpClient()

        val requestBody =
            Gson().toJson(book).toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .addHeader("Content-Type", "application/json")
            .url("http://192.168.0.100:8080/books")
            .post(requestBody)
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