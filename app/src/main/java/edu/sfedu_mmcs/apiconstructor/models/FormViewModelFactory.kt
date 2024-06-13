package edu.sfedu_mmcs.apiconstructor.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FormViewModelFactory (
    private val reqRoute: String,
    private val method: String
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FormViewModel::class.java)) {
            FormViewModel(this.reqRoute, this.method) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}