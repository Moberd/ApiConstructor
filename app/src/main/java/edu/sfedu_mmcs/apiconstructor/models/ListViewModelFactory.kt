package edu.sfedu_mmcs.apiconstructor.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class ListViewModelFactory (
    private val reqRoute: String
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ListViewModel::class.java)) {
            ListViewModel(this.reqRoute) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}