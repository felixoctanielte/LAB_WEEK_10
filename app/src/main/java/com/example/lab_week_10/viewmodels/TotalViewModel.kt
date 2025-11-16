package com.example.lab_week_10.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TotalViewModel : ViewModel() {

    // LiveData untuk menyimpan nilai total
    private val _total = MutableLiveData<Int>()
    val total: LiveData<Int> = _total

    init {
        // Inisialisasi total dengan 0
        _total.postValue(0)
    }

    // Fungsi untuk menambah nilai total
    fun incrementTotal() {
        _total.postValue(_total.value?.plus(1))
    }

    fun setTotal(newTotal: Int) {
        _total.postValue(newTotal)
    }

}
