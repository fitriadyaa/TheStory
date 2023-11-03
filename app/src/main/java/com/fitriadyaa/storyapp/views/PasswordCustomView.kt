package com.fitriadyaa.storyapp.views

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.fitriadyaa.storyapp.R

class PasswordCustomView : AppCompatEditText {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length!! < 8) {
                    error = getContext().getString(R.string.invalid_password)
                } else {
                    error = null // Clear the error message when the password is valid
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }
}
