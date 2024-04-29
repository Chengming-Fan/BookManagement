package com.fan.bookmanagement.utils

import android.content.Context
import android.widget.EditText
import by.dzmitry_lakisau.month_year_picker_dialog.MonthYearPickerDialog
import com.fan.bookmanagement.R

private const val SELECTED_YEAR = 2024
private const val MIN_YEAR = 1900
object YearPickerUtil {
    fun addYearPickDialogForEditText(context: Context, editText: EditText) {
        val dialog = MonthYearPickerDialog.Builder(
            context,
            R.style.Style_MonthYearPickerDialog_Purple,
            selectedYear = SELECTED_YEAR
        )
            .setMinYear(MIN_YEAR)
            .setMode(MonthYearPickerDialog.Mode.YEAR_ONLY)
            .setOnYearSelectedListener { year ->
                editText.setText(year.toString())
            }
            .build()
        editText.setOnClickListener {
            dialog.show()
        }
    }
}