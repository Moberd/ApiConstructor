package edu.sfedu_mmcs.apiconstructor.form_activity

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import edu.sfedu_mmcs.apiconstructor.utils.RouteInfo

class FormViewModelFactory (
    private val routeInfo: RouteInfo,
    private val sp: SharedPreferences
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FormViewModel::class.java)) {
            FormViewModel(this.routeInfo, this.sp) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}