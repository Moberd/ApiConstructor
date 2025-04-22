package edu.sfedu_mmcs.apiconstructor.models

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FormViewModelFactory (
    private val reqRoute: String,
    private val method: String,
    private val sp: SharedPreferences
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FormViewModel::class.java)) {
            FormViewModel(this.reqRoute, this.method, this.sp) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}