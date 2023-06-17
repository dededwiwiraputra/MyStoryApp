package com.mawumbo.mystoryapp.ui.login

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PasswordBoxCustom @JvmOverloads constructor (
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = androidx.appcompat.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyle) {

    private val _isError = MutableLiveData(false)
    val isError: LiveData<Boolean> = _isError

    init {
        init()
    }

    private fun init() {
        doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty()) {
                if (text.length < 8) setError(
                    "Password must be at least 8 characters", null
                )
                else error = null
            }
            _isError.postValue(error == null)
        }
    }

}