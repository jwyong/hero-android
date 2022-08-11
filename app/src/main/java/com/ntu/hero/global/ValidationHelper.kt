package com.ntu.hero.global

import android.text.TextUtils
import android.util.Patterns

class ValidationHelper {
    // basic - 0 = empty, 1 = not empty
    fun isEmpty(str: String?): Boolean {
        return str == null || str.isEmpty()
    }

    // email - 0 = empty, 1 = valid, 2 = invalid
    fun isValidEmail(emailStr: String?): Int {
        return if (isEmpty(emailStr)) {
            0
        } else {
            if (Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
                1
            } else {
                2
            }
        }
    }

    // pword - 0 = empty, 1 = valid, 2 = invalid
    fun isValidPword(pwordStr: String?, minChars: Int, maxChars: Int): Int {
        return if (isEmpty(pwordStr)) {
            0

        } else {
            if (pwordStr?.length in minChars..maxChars) {
                1

            } else {
                2
            }
        }
    }

    fun isValidInt(str: String?): Int {
        return if (isEmpty(str)) {
            0
        } else {
            if (TextUtils.isDigitsOnly(str)) {
                1
            } else {
                2
            }
        }
    }
}