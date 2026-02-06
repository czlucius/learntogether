package com.sp.learntogether.kotlin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.text.InputType
import android.view.View
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar
import java.util.Date

fun transformIntoDatePicker(textInputLayout: TextInputLayout, maxDate: Date? = null) {
//    val context = this.context
    textInputLayout.isFocusableInTouchMode = false
    textInputLayout.isClickable = true
    textInputLayout.isFocusable = false

    val myCalendar = Calendar.getInstance()
    textInputLayout.tag = myCalendar
    val datePickerOnDataSetListener =
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val sdf =
                android.text.format.DateFormat.getDateFormat(textInputLayout.context) // locale-specific date format
            textInputLayout.editText!!.setText(sdf.format(myCalendar.time))

        }

    textInputLayout.editText!!.inputType = InputType.TYPE_NULL;
    textInputLayout.editText!!.keyListener = null;
    textInputLayout.editText!!.setOnFocusChangeListener { v, hasFocus ->
        if (hasFocus) {
            v.callOnClick()
        }
    }

    textInputLayout.editText!!.setOnClickListener {
        DatePickerDialog(
            textInputLayout.context, datePickerOnDataSetListener, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).run {
            maxDate?.time?.also { datePicker.maxDate = it }
            show()
        }
    }
}

fun transformIntoTimePicker(textInputLayout: TextInputLayout) {
    textInputLayout.isFocusableInTouchMode = false
    textInputLayout.isClickable = true
    textInputLayout.isFocusable = false

//    val timePicker = MaterialTimePicker.Builder()
//        .build()
//    timePicker

    val date = Date()
    val myCalendar = Calendar.getInstance()
    textInputLayout.tag = myCalendar
    val context = textInputLayout.context

    val timeSetListener: OnTimeSetListener = OnTimeSetListener { view: View, hourOfDay: Int, minute: Int ->
        myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        myCalendar.set(Calendar.MINUTE, minute)
        val sdf =
            android.text.format.DateFormat.getTimeFormat(context) // locale-specific date format
        textInputLayout.editText!!.setText(sdf.format(myCalendar.time))
    }

    textInputLayout.editText!!.inputType = InputType.TYPE_NULL;
    textInputLayout.editText!!.keyListener = null;
    textInputLayout.editText!!.setOnFocusChangeListener { v, hasFocus ->
        if (hasFocus) {
            v.callOnClick()
        }
    }

    textInputLayout.editText!!.setOnClickListener {
        val current = Calendar.getInstance()
        TimePickerDialog(
            context,
            timeSetListener,
            current.get(Calendar.HOUR_OF_DAY), current.get(Calendar.MINUTE),
            false // just put it like that
        ).show()
    }



}