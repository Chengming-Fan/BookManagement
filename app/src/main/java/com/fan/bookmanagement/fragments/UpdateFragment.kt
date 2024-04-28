package com.fan.bookmanagement.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import by.dzmitry_lakisau.month_year_picker_dialog.MonthYearPickerDialog
import com.fan.bookmanagement.R
import com.fan.bookmanagement.databinding.FragmentUpdateBinding

class UpdateFragment : Fragment() {

    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}