package com.example.akfk_fitorf.ui.slideshow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SlideshowViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Данные из akfk.fitorf.ru"
    }
    val text: LiveData<String> = _text
}