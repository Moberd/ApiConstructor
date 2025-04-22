package edu.sfedu_mmcs.apiconstructor.models

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class ListViewModelFactory (
    private val reqRoute: String,
    private val sp: SharedPreferences
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ListViewModel::class.java)) {
            ListViewModel(this.reqRoute, this.sp) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}